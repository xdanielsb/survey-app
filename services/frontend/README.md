# Survey App - Frontend

This is the frontend for the Survey application, built with **Vue 3**, **TypeScript**, and **Vite**.

## Tech Stack

- Vue 3 (Composition API + `<script setup>`)
- TypeScript
- Vue Router
- ECharts (for data visualization)
- Vitest (unit testing)
- ESLint & Prettier (code quality & formatting)

## Getting Started

### Install dependencies

```bash
npm install
```

### Test
``` 
npm run test:unit
```

### start development server
``` 
npm run dev
```

### API Integration
The frontend expects a backend API running at /surveys/:id and /api/surveys/:id/results. You can configure proxy settings in vite.config.ts if needed.