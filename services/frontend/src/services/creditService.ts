import api from '@/services/api'

export const getUserCredits = async () => {
  const response = await api.get('/users/credits')
  return response.data
}
