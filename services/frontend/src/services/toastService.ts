import { useToast } from 'vue-toast-notification'

const toast = useToast()

export const toastService = {
  success(message: string) {
    toast.success(message)
  },

  error(message: string) {
    toast.error(message)
  },

  info(message: string) {
    toast.info(message)
  },

  warning(message: string) {
    toast.warning(message)
  },
}
