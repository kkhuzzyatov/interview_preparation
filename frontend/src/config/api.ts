export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ??
  "http://94.103.3.251:8080";


export const API_ENDPOINTS = {
  auth: {
    login: "/api/auth/login",
  },

  user: {
    register: "/api/user",
    current: "/api/user",
  },

  task: {
    list: "/api/task",

    byId: (id: string) =>
      `/api/task/${id}`,
  },

  report: {
    generate: "/api/report/generate",
  },
};