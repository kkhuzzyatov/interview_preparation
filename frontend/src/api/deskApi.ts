import {
  API_BASE_URL,
  API_ENDPOINTS,
} from "../config/api";

export interface DeskResponse {
  id: string;
  name: string;
}

export interface DeskStatisticsResponse {
  deskId: string;
  blue: number;
  red: number;
  yellow: number;
  green: number;
}

export type DeskStatisticsData =
  | DeskStatisticsResponse[]
  | Record<string, Omit<DeskStatisticsResponse, "deskId">>;

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

export async function getAllDesks(): Promise<DeskResponse[]> {
  const response = await fetch(
    API_BASE_URL + API_ENDPOINTS.desks.all,
    {
      method: "GET",
      headers: getAuthHeaders(),
    }
  );

  return handleResponse<DeskResponse[]>(response);
}

export async function getDeskStatistics(): Promise<DeskStatisticsData> {
  const response = await fetch(
    API_BASE_URL + API_ENDPOINTS.desks.statistics,
    {
      method: "GET",
      headers: getAuthHeaders(),
    }
  );

  return handleResponse<DeskStatisticsData>(response);
}