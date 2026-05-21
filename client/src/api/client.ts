const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://127.0.0.1:8000/api/v1';

export interface ApiEnvelope<T> {
  success: boolean;
  data: T;
  meta?: {
    request_id: string;
    timestamp: string;
  };
}

export async function getJson<T>(path: string): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`);
  if (!response.ok) throw new Error(`API error: ${response.status}`);
  const payload = (await response.json()) as ApiEnvelope<T>;
  return payload.data;
}

export async function postJson<TBody extends object, TData = Record<string, unknown>>(
  path: string,
  body: TBody,
): Promise<TData> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!response.ok) throw new Error(`API error: ${response.status}`);
  const payload = (await response.json()) as ApiEnvelope<TData>;
  return payload.data;
}
