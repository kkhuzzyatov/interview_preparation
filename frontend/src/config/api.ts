export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ??
  "http://localhost:8080";

export const API_ENDPOINTS = {
  auth: {
    login: "/api/auth/login",
  },

  user: {
    register: "/api/user",
    current: "/api/user",
  },

  answer: {
    all: "/api/answer",
    evaluate: (cardId: string) =>
      `/api/answer/${cardId}`,
    reveal: (cardId: string) =>
      `/api/answer/${cardId}/reveal`,
  },

  review: {
    next: "/api/review/next",
  },

  desks: {
    all: "/api/desks",
    byId: (deskId: string) =>
      `/api/desks/${deskId}`,
    statistics: "/api/desks/statistics",
  },
};