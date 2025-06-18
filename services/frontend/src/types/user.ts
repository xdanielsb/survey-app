export interface User {
  email: string
  premium: boolean
  surveyCredits: number
}

export interface UserGmail {
  email: string
  preferred_username: string
  realm_access: { roles: string[] }
  premium: boolean
}
