export interface SurveyResultQuestion {
  id: number;
  text: string;
  answers: Record<string, number>; // Likert option => count
}
