import {
  API_BASE_URL,
  API_ENDPOINTS,
} from "../config/api";

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export type UserRole = "USER" | "ADMIN";

export interface User {
  id: string;
  username: string;
  email: string;
  role: UserRole;
}

async function handleResponse<T>(
  response: Response,
): Promise<T> {
  if (!response.ok) {
    const error: unknown = await response
      .json()
      .catch(() => ({
        message: "Request failed",
      }));

    if (
      typeof error === "object" &&
      error !== null &&
      "message" in error &&
      typeof error.message === "string"
    ) {
      throw new Error(error.message);
    }

    throw new Error("Request failed");
  }

  const contentType =
    response.headers.get("content-type");

  if (
    !contentType ||
    !contentType.includes("application/json")
  ) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export async function register(
  data: RegisterRequest,
): Promise<User> {
  const response = await fetch(
    `${API_BASE_URL}${API_ENDPOINTS.user.register}`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    },
  );

  return handleResponse<User>(response);
}

export async function getCurrentUser(
  token: string,
): Promise<User> {
  const response = await fetch(
    `${API_BASE_URL}${API_ENDPOINTS.user.current}`,
    {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    },
  );

  return handleResponse<User>(response);
}