import {
  API_BASE_URL,
  API_ENDPOINTS,
} from "../config/api";

export interface DeskResponse {
  id: string;
  name: string;
  topicId: string;
}

export interface DeskWithCardsResponse {
  id: string;
  name: string;
  cards: unknown[];
}

export interface DeskStatisticsResponse {
  totalDesks: number;
  totalCards: number;
  totalAnswers: number;
  averageScore: number;
}

export interface CreateDeskRequest {
  name: string;
  topicId: string;
}

export interface UpdateDeskRequest {
  name: string;
  topicId: string;
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

  if (response.status === 204) {
    return undefined as T;
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

async function get<T>(
  endpoint: string,
): Promise<T> {
  const response = await fetch(
    `${API_BASE_URL}${endpoint}`,
    {
      method: "GET",
      headers: getAuthHeaders(),
    },
  );

  return handleResponse<T>(response);
}

async function post<TRequest, TResponse>(
  endpoint: string,
  body: TRequest,
): Promise<TResponse> {
  const response = await fetch(
    `${API_BASE_URL}${endpoint}`,
    {
      method: "POST",
      headers: {
        ...getAuthHeaders(),
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    },
  );

  return handleResponse<TResponse>(response);
}

async function put<TRequest, TResponse>(
  endpoint: string,
  body: TRequest,
): Promise<TResponse> {
  const response = await fetch(
    `${API_BASE_URL}${endpoint}`,
    {
      method: "PUT",
      headers: {
        ...getAuthHeaders(),
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    },
  );

  return handleResponse<TResponse>(response);
}

async function del(
  endpoint: string,
): Promise<void> {
  const response = await fetch(
    `${API_BASE_URL}${endpoint}`,
    {
      method: "DELETE",
      headers: getAuthHeaders(),
    },
  );

  await handleResponse<void>(response);
}

/* =========================
   GET
   ========================= */

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

export function getDeskStatistics(): Promise<DeskStatisticsResponse> {
  return get<DeskStatisticsResponse>(
    API_ENDPOINTS.desks.statistics,
  );
}

/* =========================
   POST
   ========================= */

export function createDesk(
  request: CreateDeskRequest,
): Promise<DeskResponse> {
  return post<CreateDeskRequest, DeskResponse>(
    API_ENDPOINTS.desks.all,
    request,
  );
}

/* =========================
   PUT
   ========================= */

export function updateDesk(
  deskId: string,
  request: UpdateDeskRequest,
): Promise<DeskResponse> {
  return put<UpdateDeskRequest, DeskResponse>(
    API_ENDPOINTS.desks.byId(deskId),
    request,
  );
}

/* =========================
   DELETE
   ========================= */

export function deleteDesk(
  deskId: string,
): Promise<void> {
  return del(
    API_ENDPOINTS.desks.byId(deskId),
  );
}