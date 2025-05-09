import axios from 'axios'
import type { Survey } from '@/types/survey'
import type { SurveyResults } from '@/types/survey-results'
import type { SurveyResponseDTO } from '@/types/survey-response-dto'

const baseUrl = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080'
const api = axios.create({
  baseURL: baseUrl,
})

export const fetchSurveys = async (): Promise<Survey[]> => {
  const response = await api.get<Survey[]>('/surveys')
  return response.data
}

export const fetchSurveyById = async (id: string | number): Promise<Survey> => {
  const response = await api.get<Survey>(`/surveys/${id}`)
  return response.data
}

export const submitSurveyResponse = async (
  id: string | number,
  response: SurveyResponseDTO,
): Promise<void> => {
  await api.post(`/surveys/${id}/responses`, response)
}

export const fetchSurveyResults = async (id: number): Promise<SurveyResults> => {
  const response = await api.get(`/surveys/${id}/results`)
  return response.data
}
