import type { Survey } from '@/types/survey'
import type { SurveyResults } from '@/types/survey-results'
import type { SurveyResponse } from '@/types/survey-response.ts'
import type { CreateSurveyInput } from '@/types/create-question-input.ts'
import type { Page } from '@/types/pagination.ts'
import api from '@/services/api'
import type { CreateUserInput } from '@/types/create-user-input.ts'
import type { User } from '@/types/user.ts'

export const fetchSurveys = async (
  page: number = 0,
  size: number = 5,
  query = '',
): Promise<Page<Survey>> => {
  const response = await api.get<Page<Survey>>('/surveys', {
    params: { page, size, q: query },
  })
  return response.data
}

export const fetchSurveyById = async (id: string | number): Promise<Survey> => {
  const response = await api.get<Survey>(`/surveys/${id}`)
  return response.data
}

export const submitSurveyResponse = async (
  id: string | number,
  response: SurveyResponse,
): Promise<void> => {
  await api.post(`/surveys/${id}/responses`, response)
}

export const fetchSurveyResults = async (id: number): Promise<SurveyResults> => {
  const response = await api.get(`/surveys/${id}/results`)
  return response.data
}

export const createSurvey = async (data: CreateSurveyInput): Promise<Survey> => {
  const response = await api.post<Survey>('/surveys/create', data)
  return response.data
}

export const deleteSurvey = async (surveyId: number): Promise<void> => {
  await api.delete(`/surveys/delete/${surveyId}`)
}

export const createUser = async (data: CreateUserInput): Promise<User> => {
  const response = await api.post<User>('/users/create', data)
  return response.data
}
