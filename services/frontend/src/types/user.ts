export interface User {
  email: string
  roles: string[]
  premium: boolean
}

export interface UserGmail {
  email: string
  preferred_username: string
  realm_access: { roles: string[] }
  premium: boolean
}
