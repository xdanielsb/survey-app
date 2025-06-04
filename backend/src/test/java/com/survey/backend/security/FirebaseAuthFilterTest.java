package com.survey.backend.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.survey.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class FirebaseAuthFilterTest {

  private FirebaseAuthFilter filter;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private FilterChain chain;
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    filter = new FirebaseAuthFilter(userRepository);
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    chain = mock(FilterChain.class);

    // Clear auth context between tests
    SecurityContextHolder.clearContext();
  }

  @Test
  @WithMockUser(username = "user123")
  void shouldSetAuthentication_whenValidToken() throws Exception {
    String uid = "user123";
    FirebaseToken mockToken = mock(FirebaseToken.class);
    when(mockToken.getUid()).thenReturn(uid);

    when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");

    try (MockedStatic<FirebaseAuth> firebaseAuthMock = mockStatic(FirebaseAuth.class)) {
      FirebaseAuth firebaseAuth = mock(FirebaseAuth.class);
      when(firebaseAuth.verifyIdToken("valid-token")).thenReturn(mockToken);
      firebaseAuthMock.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);

      filter.doFilterInternal(request, response, chain);

      assertThat(SecurityContextHolder.getContext().getAuthentication())
          .isNotNull()
          .extracting(auth -> auth.getPrincipal())
          .isEqualTo(uid);

      verify(chain).doFilter(request, response);
    }
  }

  @Test
  void shouldNotSetAuthentication_whenInvalidToken() throws Exception {
    when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");

    try (MockedStatic<FirebaseAuth> firebaseAuthMock = mockStatic(FirebaseAuth.class)) {
      FirebaseAuth firebaseAuth = mock(FirebaseAuth.class);

      FirebaseAuthException mockException = mock(FirebaseAuthException.class);
      when(mockException.getMessage()).thenReturn("Mock invalid token");
      when(firebaseAuth.verifyIdToken("invalid-token")).thenThrow(mockException);
      firebaseAuthMock.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);

      filter.doFilterInternal(request, response, chain);
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
      verify(chain).doFilter(request, response);
    }
  }

  @Test
  void shouldNotSetAuthentication_whenNoAuthorizationHeader() throws Exception {
    when(request.getHeader("Authorization")).thenReturn(null);

    filter.doFilterInternal(request, response, chain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(chain).doFilter(request, response);
  }

  @Test
  void shouldSetAuthentication_whenTokenInCookie() throws Exception {
    String uid = "userCookie";
    FirebaseToken mockToken = mock(FirebaseToken.class);
    when(mockToken.getUid()).thenReturn(uid);

    when(request.getHeader("Authorization")).thenReturn(null);
    Cookie cookie = new Cookie("token", "cookie-token");
    when(request.getCookies()).thenReturn(new Cookie[] {cookie});

    try (MockedStatic<FirebaseAuth> firebaseAuthMock = mockStatic(FirebaseAuth.class)) {
      FirebaseAuth firebaseAuth = mock(FirebaseAuth.class);
      when(firebaseAuth.verifyIdToken("cookie-token")).thenReturn(mockToken);
      firebaseAuthMock.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);

      filter.doFilterInternal(request, response, chain);

      assertThat(SecurityContextHolder.getContext().getAuthentication())
              .isNotNull()
              .extracting(auth -> auth.getPrincipal())
              .isEqualTo(uid);

      verify(chain).doFilter(request, response);
    }
  }
}
