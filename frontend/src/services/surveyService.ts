import axios from 'axios';
import type { Survey } from '@/types/survey';

export const fetchSurveys = async (): Promise<Survey[]> => {
  const response = await axios.get<Survey[]>('/surveys');
  return response.data;
};
