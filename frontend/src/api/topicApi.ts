import {
  API_BASE_URL,
  API_ENDPOINTS,
} from "../config/api";

export interface TopicResponse {
  id: string;
  name: string;
}

export interface CreateTopicRequest {
  name: string;
}

export interface UpdateTopicRequest {
  name: string;
}

async function handleResponse<T>(
  response: Response,
): Promise<T> {
  if (!response.ok) {
    let message = "Request failed";

    try {
      const error: unknown =
        await response.json();

      if (
        typeof error === "object" &&
        error !== null &&
        "message" in error &&
        typeof error.message === "string"
      ) {
        message = error.message;
      } else if (
        typeof error === "string"
      ) {
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

  return response.json() as Promise<T>;
}

function getAuthHeaders(): HeadersInit {
  const token =
    localStorage.getItem("token");

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

  return handleResponse<TResponse>(
    response,
  );
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

  return handleResponse<TResponse>(
    response,
  );
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

  if (!response.ok) {
    await handleResponse<unknown>(
      response,
    );
  }
}

export function getAllTopics(): Promise<
  TopicResponse[]
> {
  return get<TopicResponse[]>(
    API_ENDPOINTS.topics.all,
  );
}

export function getTopicById(
  topicId: string,
): Promise<TopicResponse> {
  return get<TopicResponse>(
    API_ENDPOINTS.topics.byId(topicId),
  );
}

export function createTopic(
  request: CreateTopicRequest,
): Promise<TopicResponse> {
  return post<
    CreateTopicRequest,
    TopicResponse
  >(
    API_ENDPOINTS.topics.all,
    request,
  );
}

export function updateTopic(
  topicId: string,
  request: UpdateTopicRequest,
): Promise<TopicResponse> {
  return put<
    UpdateTopicRequest,
    TopicResponse
  >(
    API_ENDPOINTS.topics.byId(topicId),
    request,
  );
}

export function deleteTopic(
  topicId: string,
): Promise<void> {
  return del(
    API_ENDPOINTS.topics.byId(topicId),
  );
}