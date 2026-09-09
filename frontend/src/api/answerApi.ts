import {
  API_BASE_URL,
  API_ENDPOINTS,
} from "../config/api";

export interface AnswerHistoryResponse {
  id: string;
  cardId: string;
  question: string;
  correctAnswer: string;
  userAnswer: string | null;
  aiFeedback: string | null;
  score: number;
  startAnswerTime: string | null;
  submissionTime: string | null;
  aiProcessingDurationMs: number | null;
  createdAt: string;
  deskId: string;
  deskName: string;
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

export async function startAnswer(
  cardId: string
): Promise<void> {
  const token = localStorage.getItem("token");

  if (!token) {
    throw new Error(
      "Неавторизован или токен истёк"
    );
  }

  const response = await fetch(
    API_BASE_URL + API_ENDPOINTS.answer.start(cardId),
    {
      method: "POST",
      headers: getAuthHeaders(),
    }
  );

  await handleResponse<void>(response);
}

export async function getAllAnswers(): Promise<
  AnswerHistoryResponse[]
> {
  const token = localStorage.getItem("token");

  if (!token) {
    throw new Error(
      "Неавторизован или токен истёк"
    );
  }

  const response = await fetch(
    API_BASE_URL + API_ENDPOINTS.answer.all,
    {
      method: "GET",
      headers: getAuthHeaders(),
    }
  );

  return handleResponse<AnswerHistoryResponse[]>(
    response
  );
}