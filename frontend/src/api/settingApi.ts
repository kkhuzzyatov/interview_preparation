import {
  API_BASE_URL,
  API_ENDPOINTS,
} from "../config/api";

export interface ApplicationSettings {
  id: number;
  answerEvaluationPrompt: string;

  evaluationRedAverageScore: number;
  evaluationGreenMinAnswers: number;
  evaluationGreenAverageScore: number;

  reviewMultiplierRecencyDefaultMultiplier: number;
  reviewMultiplierMeetChanceMin: number;
  reviewMultiplierMeetChanceMax: number;
  reviewMultiplierMinScore: number;
  reviewMultiplierMaxScore: number;
  reviewMultiplierBaseDifficultyMultiplier: number;
  reviewMultiplierDefaultDifficultyMultiplier: number;
}

export interface SettingsRequest {
  answerEvaluationPrompt: string;

  evaluationRedAverageScore: number;
  evaluationGreenMinAnswers: number;
  evaluationGreenAverageScore: number;

  reviewMultiplierRecencyDefaultMultiplier: number;
  reviewMultiplierMeetChanceMin: number;
  reviewMultiplierMeetChanceMax: number;
  reviewMultiplierMinScore: number;
  reviewMultiplierMaxScore: number;
  reviewMultiplierBaseDifficultyMultiplier: number;
  reviewMultiplierDefaultDifficultyMultiplier: number;
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

    if (response.status === 403) {
      message = "Доступ разрешён только администратору";
    }

    if (response.status === 404) {
      message = "Настройки не найдены";
    }

    throw new Error(message);
  }

  if (response.status === 204) {
    return undefined as T;
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

function requireToken(): void {
  const token = localStorage.getItem("token");

  if (!token) {
    throw new Error(
      "Неавторизован или токен истёк"
    );
  }
}

export async function getSettings(): Promise<ApplicationSettings> {
  requireToken();

  const response = await fetch(
    API_BASE_URL + API_ENDPOINTS.settings.all,
    {
      method: "GET",
      headers: getAuthHeaders(),
    }
  );

  return handleResponse<ApplicationSettings>(
    response
  );
}

export async function createSettings(
  settings: SettingsRequest
): Promise<ApplicationSettings> {
  requireToken();

  const response = await fetch(
    API_BASE_URL + API_ENDPOINTS.settings.all,
    {
      method: "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify(settings),
    }
  );

  return handleResponse<ApplicationSettings>(
    response
  );
}

export async function updateSettings(
  settings: SettingsRequest
): Promise<ApplicationSettings> {
  requireToken();

  const response = await fetch(
    API_BASE_URL + API_ENDPOINTS.settings.all,
    {
      method: "PUT",
      headers: getAuthHeaders(),
      body: JSON.stringify(settings),
    }
  );

  return handleResponse<ApplicationSettings>(
    response
  );
}

export async function deleteSettings(): Promise<void> {
  requireToken();

  const response = await fetch(
    API_BASE_URL + API_ENDPOINTS.settings.all,
    {
      method: "DELETE",
      headers: getAuthHeaders(),
    }
  );

  await handleResponse<void>(response);
}