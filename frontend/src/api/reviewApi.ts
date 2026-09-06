import {
  API_BASE_URL,
  API_ENDPOINTS,
} from "../config/api";

export interface ReviewCardResponse {
  cardId: string;
  deskId: string;
  deskName: string;
  question: string;
  answer: string;
}

export interface AnswerRequest {
  answer: string;
}

export interface AnswerResponse {
  score: number;
  feedback: string;
  correctAnswer: string;
}

export interface RevealAnswerResponse {
  correctAnswer: string;
}

async function handleResponse<T>(
  response: Response
): Promise<T> {
  if (!response.ok) {
    const error = await response
      .json()
      .catch(() => ({
        message: "Request failed",
      }));

    throw new Error(
      error.message || "Request failed"
    );
  }

  return response.json();
}

function getAuthHeaders(
  contentType?: string
): Record<string, string> {
  const token = localStorage.getItem("token");

  const headers: Record<string, string> = {};

  if (contentType) {
    headers["Content-Type"] = contentType;
  }

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  return headers;
}

export async function getNextReviewCard(): Promise<ReviewCardResponse> {
  const response = await fetch(
    API_BASE_URL + API_ENDPOINTS.review.next,
    {
      method: "GET",
      headers: getAuthHeaders(),
    }
  );

  return handleResponse<ReviewCardResponse>(
    response
  );
}

export async function submitAnswer(
  cardId: string,
  answer: string
): Promise<AnswerResponse> {
  const response = await fetch(
    API_BASE_URL +
      API_ENDPOINTS.answer.evaluate(cardId),
    {
      method: "POST",
      headers: getAuthHeaders("application/json"),
      body: JSON.stringify({
        answer,
      }),
    }
  );

  return handleResponse<AnswerResponse>(
    response
  );
}

export async function revealAnswer(
  cardId: string
): Promise<RevealAnswerResponse> {
  const response = await fetch(
    API_BASE_URL +
      API_ENDPOINTS.answer.reveal(cardId),
    {
      method: "POST",
      headers: getAuthHeaders(),
    }
  );

  const data =
    await handleResponse<RevealAnswerResponse>(
      response
    );

  console.log(
    "Reveal response from server:",
    data
  );

  return data;
}