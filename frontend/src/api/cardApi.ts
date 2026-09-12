import {
  API_BASE_URL,
  API_ENDPOINTS,
} from "../config/api";

export interface CardResponse {
  id: string;
  question: string;
  answer: string;
  deskId: string;
}

export interface UpdateCardRequest {
  question: string;
  answer: string;
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

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  return headers;
}

export async function updateCard(
  cardId: string,
  card: UpdateCardRequest
): Promise<CardResponse> {
  const token = localStorage.getItem("token");

  if (!token) {
    throw new Error(
      "Неавторизован или токен истёк"
    );
  }

  const response = await fetch(
    API_BASE_URL +
      API_ENDPOINTS.cards.update(cardId),
    {
      method: "PUT",
      headers: getAuthHeaders(),
      body: JSON.stringify(card),
    }
  );

  return handleResponse<CardResponse>(
    response
  );
}