import type { Question } from '@/types/question.ts'

export interface Survey {
  id: number;
  title: string;
  questions: Question[];
}
