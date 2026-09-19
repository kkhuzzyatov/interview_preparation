import {
  API_BASE_URL,
  API_ENDPOINTS,
} from "../config/api";

export interface DeskResponse {
  id: string;
  name: string;
}

export interface CardResponse {
  id: string;
  question: string;
  answer: string;
}

export interface DeskWithCardsResponse
  extends DeskResponse {
  cards: CardResponse[];
}

export interface ScoreColorStatistics {
  colorHex: string;
  count: number;
}

export interface DeskCardLevelStatisticsResponse {
  deskId: string;
  deskName: string;
  statistics: ScoreColorStatistics[];
}

async function handleResponse<T>(
  response: Response,
): Promise<T> {
  if (!response.ok) {
    let message = "Request failed";

    try {
      const error: unknown = await response.json();

      if (
        typeof error === "object" &&
        error !== null &&
        "message" in error &&
        typeof error.message === "string"
      ) {
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

  return response.json() as Promise<T>;
}

function getAuthHeaders(): HeadersInit {
  const token = localStorage.getItem("token");

  return token
    ? {
        Authorization: `Bearer ${token}`,
      }
    : {};
}

async function get<T>(endpoint: string): Promise<T> {
  const response = await fetch(
    `${API_BASE_URL}${endpoint}`,
    {
      method: "GET",
      headers: getAuthHeaders(),
    },
  );

  return handleResponse<T>(response);
}

export function getAllDesks(): Promise<DeskResponse[]> {
  return get<DeskResponse[]>(
    API_ENDPOINTS.desks.all,
  );
}

export function getDeskById(
  deskId: string,
): Promise<DeskWithCardsResponse> {
  return get<DeskWithCardsResponse>(
    API_ENDPOINTS.desks.byId(deskId),
  );
}

export function getDeskStatistics(): Promise<
  DeskCardLevelStatisticsResponse[]
> {
  return get<DeskCardLevelStatisticsResponse[]>(
    API_ENDPOINTS.desks.statistics,
  );
}