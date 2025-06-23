import { describe, it, expect, vi, beforeEach } from 'vitest'

let keycloakInstance: any
const keycloakConstructor = vi.fn(() => keycloakInstance)

vi.mock('keycloak-js', () => ({
  default: keycloakConstructor,
}))

const jwtDecodeMock = vi.fn()
vi.mock('jwt-decode', () => ({ jwtDecode: jwtDecodeMock }))

const useAuthStoreMock = vi.fn()
vi.mock('@/stores/authStore', () => ({ useAuthStore: useAuthStoreMock }))

describe('keycloak.ts', () => {
  let keycloakModule: typeof import('@/keycloak.ts')

  beforeEach(async () => {
    vi.resetModules()
    vi.clearAllMocks()
    keycloakInstance = { init: vi.fn(), authenticated: false, token: null }
    keycloakModule = await import('@/keycloak.ts')
  })

  it('initializeKeycloak calls init with expected options', async () => {
    await keycloakModule.initializeKeycloak()
    expect(keycloakInstance.init).toHaveBeenCalledWith({
      onLoad: 'check-sso',
      pkceMethod: 'S256',
      flow: 'standard',
      checkLoginIframe: false,
    })
  })

  it('syncAuthFromKeycloak calls login with decoded token', () => {
    const loginMock = vi.fn()
    useAuthStoreMock.mockReturnValue({ login: loginMock, isPremium: true })
    keycloakInstance.authenticated = true
    keycloakInstance.token = 'abc.def'
    jwtDecodeMock.mockReturnValue({
      email: 'x@y.com',
      preferred_username: 'x@y.com',
      realm_access: { roles: ['user'] },
    })

    keycloakModule.syncAuthFromKeycloak()

    expect(loginMock).toHaveBeenCalledWith('x@y.com', 'abc.def', ['user'], true)
  })

  it('syncAuthFromKeycloak does nothing when not authenticated', () => {
    const loginMock = vi.fn()
    useAuthStoreMock.mockReturnValue({ login: loginMock, isPremium: false })

    keycloakModule.syncAuthFromKeycloak()

    expect(loginMock).not.toHaveBeenCalled()
  })
})
