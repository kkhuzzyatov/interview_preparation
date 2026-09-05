import {
  API_BASE_URL,
  API_ENDPOINTS,
} from "../config/api";

export interface ReviewCardResponse {
  cardId: string;
  deskId: string;
  deskName: string;
  question: string;
}

export interface AnswerRequest {
  answer: string;
}

export interface AnswerResponse {
  score: number;
  feedback: string;
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

export async function getNextReviewCard(): Promise<ReviewCardResponse> {
  const token = localStorage.getItem("token");

  const headers: Record<string, string> = {};

  if (token) {
    headers.Authorization = "Bearer " + token;
  }

  const response = await fetch(
    API_BASE_URL + API_ENDPOINTS.review.next,
    {
      method: "GET",
      headers,
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
  const token = localStorage.getItem("token");

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
  };

  if (token) {
    headers.Authorization = "Bearer " + token;
  }

  const response = await fetch(
    API_BASE_URL +
      API_ENDPOINTS.answer.evaluate(cardId),
    {
      method: "POST",
      headers,
      body: JSON.stringify({
        answer,
      }),
    }
  );

  return handleResponse<AnswerResponse>(
    response
  );
}