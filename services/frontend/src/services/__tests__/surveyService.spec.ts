import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as service from '../surveyService'
import api from '../api'
import type { CreateSurveyInput } from '@/types/create-question-input.ts'

vi.mock('../api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    delete: vi.fn(),
  },
}))

const apiMock = api as unknown as {
  get: ReturnType<typeof vi.fn>
  post: ReturnType<typeof vi.fn>
  delete: ReturnType<typeof vi.fn>
}

describe('surveyService', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('fetchSurveys sends correct query params', async () => {
    apiMock.get.mockResolvedValue({ data: { items: [] } })
    const result = await service.fetchSurveys(2, 7, 'foo')
    expect(apiMock.get).toHaveBeenCalledWith('/surveys', {
      params: { page: 2, size: 7, q: 'foo' },
    })
    expect(result).toEqual({ items: [] })
  })

  it('fetchSurveyById calls endpoint with id', async () => {
    apiMock.get.mockResolvedValue({ data: { id: 5 } })
    const result = await service.fetchSurveyById(5)
    expect(apiMock.get).toHaveBeenCalledWith('/surveys/5')
    expect(result).toEqual({ id: 5 })
  })

  it('submitSurveyResponse posts responses', async () => {
    apiMock.post.mockResolvedValue({})
    await service.submitSurveyResponse(3, { surveyId: 3, answers: [] })
    expect(apiMock.post).toHaveBeenCalledWith('/surveys/3/responses', { answers: [], surveyId: 3 })
  })

  it('fetchSurveyResults retrieves results', async () => {
    apiMock.get.mockResolvedValue({ data: { id: 1, questions: [] } })
    const result = await service.fetchSurveyResults(1)
    expect(apiMock.get).toHaveBeenCalledWith('/surveys/1/results')
    expect(result).toEqual({ id: 1, questions: [] })
  })

  it('createSurvey posts new survey data', async () => {
    apiMock.post.mockResolvedValue({ data: { id: 9 } })
    const result = await service.createSurvey({ title: 'x' } as CreateSurveyInput)
    expect(apiMock.post).toHaveBeenCalledWith('/surveys/create', { title: 'x' })
    expect(result).toEqual({ id: 9 })
  })

  it('deleteSurvey issues delete request', async () => {
    apiMock.delete.mockResolvedValue({})
    await service.deleteSurvey(4)
    expect(apiMock.delete).toHaveBeenCalledWith('/surveys/delete/4')
  })

  it('fetchUser retrieves user information', async () => {
    apiMock.get.mockResolvedValue({ data: { email: 'a@b.com' } })
    const result = await service.fetchUser()
    expect(apiMock.get).toHaveBeenCalledWith('/users/fetch', {})
    expect(result).toEqual({ email: 'a@b.com' })
  })

  it('askSurveyQuestion posts question and returns answer', async () => {
    apiMock.post.mockResolvedValue({ data: { answer: '42' } })
    const result = await service.askSurveyQuestion(1, 'Why?')
    expect(apiMock.post).toHaveBeenCalledWith('/surveys/1/chat', { question: 'Why?' })
    expect(result).toEqual({ answer: '42' })
  })
})
