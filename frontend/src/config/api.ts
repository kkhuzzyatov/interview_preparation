export const API_BASE_URL =
import.meta.env.VITE_API_BASE_URL ??
"http://144.31.213.133:8080/api";

export const API_ENDPOINTS = {
    auth: {
        login: "/auth/login",
    },

    user: {
        register: "/user",
        current: "/user",
    },

    answer: {
        evaluate: (cardId: string) =>
        `/answer/${cardId}`,

        reveal: (cardId: string) =>
        `/answer/${cardId}/reveal`,

        all: "/answer",
    },

    review: {
        next: "/review/next",
    },

    desks: {
        all: "/desks",
        byId: (deskId: string) =>
        `/desks/${deskId}`,
        statistics: "/desks/statistics",
    },

    leaderboard: {
        day: "/leaderboard/day",
        week: "/leaderboard/week",
        allTime: "/leaderboard/all-time",
    },
};