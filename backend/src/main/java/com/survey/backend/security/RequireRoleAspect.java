package com.survey.backend.security;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RequireRoleAspect {

  private static final Logger log = LoggerFactory.getLogger(RequireRoleAspect.class);

  @Before("@annotation(requireRole)")
  public void checkRoles(JoinPoint joinPoint, RequireRole requireRole) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || !auth.isAuthenticated()) {
      log.error("User is not authenticated for method: {}", joinPoint.getSignature());
      throw new SecurityException("User is not authenticated");
    }

    Set<String> userRoles =
        auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

    boolean hasMatch =
        Arrays.stream(requireRole.value()).map(Enum::name).anyMatch(userRoles::contains);

    if (!hasMatch) {
      log.error(
          "User does not have required role(s): {} for method: {}",
          Arrays.toString(requireRole.value()),
          joinPoint.getSignature());
      throw new SecurityException(
          "User does not have required role(s): " + Arrays.toString(requireRole.value()));
    }
  }
}
