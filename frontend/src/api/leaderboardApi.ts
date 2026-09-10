import {
  API_BASE_URL,
  API_ENDPOINTS,
} from "../config/api";

export interface LeaderboardUserResponse {
  userId: string;
  username: string;
  totalScore: number;
  answerCount: number;
}

async function handleResponse<T>(
  response: Response
): Promise<T> {
  if (!response.ok) {
    let message = "Request failed";

    try {
      const error = await response.json();

      if (error.message) {
        message = error.message;
      } else if (typeof error === "string") {
        message = error;
      }
    } catch {
      // Response does not contain JSON.
    }

    if (response.status === 401) {
      message = "Неавторизован или токен истёк";
    }

    throw new Error(message);
  }

  return response.json();
}

function getAuthHeaders(): Record<string, string> {
  const token = localStorage.getItem("token");

  const headers: Record<string, string> = {};

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  return headers;
}

async function getLeaderboard(
  endpoint: string
): Promise<LeaderboardUserResponse[]> {
  const token = localStorage.getItem("token");

  if (!token) {
    throw new Error(
      "Неавторизован или токен истёк"
    );
  }

  const response = await fetch(
    API_BASE_URL + endpoint,
    {
      method: "GET",
      headers: getAuthHeaders(),
    }
  );

  return handleResponse<LeaderboardUserResponse[]>(
    response
  );
}

export async function getDayLeaderboard(): Promise<
  LeaderboardUserResponse[]
> {
  return getLeaderboard(
    API_ENDPOINTS.leaderboard.day
  );
}

export async function getWeekLeaderboard(): Promise<
  LeaderboardUserResponse[]
> {
  return getLeaderboard(
    API_ENDPOINTS.leaderboard.week
  );
}

export async function getAllTimeLeaderboard(): Promise<
  LeaderboardUserResponse[]
> {
  return getLeaderboard(
    API_ENDPOINTS.leaderboard.allTime
  );
}