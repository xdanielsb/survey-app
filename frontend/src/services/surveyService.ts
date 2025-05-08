import axios from 'axios';
import type { Survey } from '@/types/survey';
import type { SurveyResults } from '@/types/survey-results.ts'
import type { SurveyResponseDTO } from '@/types/survey-response-dto.ts'

export const fetchSurveys = async (): Promise<Survey[]> => {
  const response = await axios.get<Survey[]>('/surveys');
  return response.data;
};

export const fetchSurveyById = async (id: string | number): Promise<Survey> => {
  const response = await axios.get<Survey>(`/surveys/${id}`)
  return response.data
}

export const submitSurveyResponse = async (id: string | number, response: SurveyResponseDTO): Promise<void> => {
  await axios.post(`/surveys/${id}/responses`, response)
}

export const fetchSurveyResults = async (id: number): Promise<SurveyResults> => {
  const response = await axios.get(`/surveys/${id}/results`);
  return response.data;
};
