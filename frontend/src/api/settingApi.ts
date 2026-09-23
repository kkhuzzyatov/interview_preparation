import {
  API_BASE_URL,
  API_ENDPOINTS,
} from "../config/api";

export interface DifficultyMultiplier {
  difficultyMultiplierId?: number;
  lastAnswerScoreBorder: number;
  multiplier: number;
}

export interface MeetChanceMultiplier {
  meetChanceMultiplierId?: number;
  meetChanceBorder: number;
  multiplier: number;
}

export interface RecencyMultiplier {
  recencyMultiplierId?: number;
  secondsBorder: number;
  multiplier: number;
}

export interface ScoreColor {
  scoreColorsId?: number;
  score: number;
  colorHex: string;
}

export interface ApplicationSettings {
  answerEvaluationPrompt: string;
  newCardRecencyMultiplier: number;
  newCardDifficultyMultiplier: number;
  newCardColor: string;
  difficultyMultipliers: DifficultyMultiplier[];
  meetChanceMultipliers: MeetChanceMultiplier[];
  recencyMultipliers: RecencyMultiplier[];
  scoreColor: ScoreColor[];
}

export interface SettingsRequest {
  answerEvaluationPrompt: string;
  newCardRecencyMultiplier: number;
  newCardDifficultyMultiplier: number;
  newCardColor: string;
  difficultyMultipliers: DifficultyMultiplier[];
  meetChanceMultipliers: MeetChanceMultiplier[];
  recencyMultipliers: RecencyMultiplier[];
  scoreColor: ScoreColor[];
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
    throw new Error("Неавторизован или токен истёк");
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

  return handleResponse<ApplicationSettings>(response);
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

  return handleResponse<ApplicationSettings>(response);
}