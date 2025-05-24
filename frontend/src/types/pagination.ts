export interface Page<T> {
  content: T[]
  totalPages: number
  totalElements: number
  number: number // current page
  size: number
}
