export interface User {
  id: number;
  uid: string;
  email: string;
  surveyCredits: number;
  premium: boolean;
  roles: string[];
}
