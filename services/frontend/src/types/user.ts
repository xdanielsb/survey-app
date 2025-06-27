export interface User {
  email: string
  premium: boolean
  surveyCredits: number
}

export interface UserGmail {
  email: string
  preferred_username: string
  given_name: string
  family_name: string
  realm_access: { roles: string[] }
  premium: boolean
  exp: number // token expiry (Unix timestamp)
}
