import axios from 'axios';
import type { Survey } from '@/types/survey';

export const fetchSurveys = async (): Promise<Survey[]> => {
  const response = await axios.get<Survey[]>('/surveys');
  return response.data;
};

export const fetchSurveyById = async (id: string | number): Promise<Survey> => {
  const response = await axios.get<Survey>(`/surveys/${id}`)
  return response.data
}

export const submitSurveyResponse = async (id: string | number, response: any): Promise<void> => {
  await axios.post(`/surveys/${id}/responses`, response)
}
