import {
  API_BASE_URL,
  API_ENDPOINTS,
} from "../config/api";

export interface CardResponse {
  id: string;
  question: string;
  answer: string;
  deskId: string;
  meetChance: number;
}

export interface CreateCardRequest {
  question: string;
  answer: string;
  deskId: string;
}

export interface CreateCardResponse {
  id: string;
  question: string;
  answer: string;
  deskId: string;
  meetChance: number;
}

export interface UpdateCardRequest {
  question: string;
  answer: string;
}

export interface UpdateCardResponse {
  id: string;
  question: string;
  answer: string;
  deskId: string;
}

const UNAUTHORIZED_MESSAGE = "Неавторизован или токен истёк";

function getAuthHeaders(): Record<string, string> {
  const token = localStorage.getItem("token");

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  return headers;
}

function requireToken(): void {
  if (!localStorage.getItem("token")) {
    throw new Error(UNAUTHORIZED_MESSAGE);
  }
}

async function handleResponse<T>(
  response: Response,
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
      message = UNAUTHORIZED_MESSAGE;
    }

    throw new Error(message);
  }

  return response.json();
}

async function handleVoidResponse(
  response: Response,
): Promise<void> {
  if (!response.ok) {
    await handleResponse<unknown>(response);
    return;
  }
}

export async function createCard(
  request: CreateCardRequest,
): Promise<CreateCardResponse> {
  requireToken();

  const response = await fetch(
    API_BASE_URL + API_ENDPOINTS.cards.all,
    {
      method: "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify(request),
    },
  );

  return handleResponse<CreateCardResponse>(response);
}

export async function updateCard(
  cardId: string,
  request: UpdateCardRequest,
): Promise<UpdateCardResponse> {
  requireToken();

  const response = await fetch(
    API_BASE_URL + API_ENDPOINTS.cards.update(cardId),
    {
      method: "PUT",
      headers: getAuthHeaders(),
      body: JSON.stringify(request),
    },
  );

  return handleResponse<UpdateCardResponse>(response);
}

export async function deleteCard(
  cardId: string,
): Promise<void> {
  requireToken();

  const response = await fetch(
    API_BASE_URL + API_ENDPOINTS.cards.delete(cardId),
    {
      method: "DELETE",
      headers: getAuthHeaders(),
    },
  );

  await handleVoidResponse(response);
}