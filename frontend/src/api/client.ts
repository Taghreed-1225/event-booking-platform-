import type { ApiError, Availability, Booking, Event, EventStatus, TokenResponse } from "../types";

const EVENT_SERVICE_URL = "http://localhost:8081";
const BOOKING_SERVICE_URL = "http://localhost:8082";

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    const error: ApiError = await res.json();
    throw error;
  }
  return res.json() as Promise<T>;
}

function authHeaders(token: string): HeadersInit {
  return { "Content-Type": "application/json", Authorization: `Bearer ${token}` };
}

export function register(username: string, password: string, role: string): Promise<TokenResponse> {
  return fetch(`${EVENT_SERVICE_URL}/api/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password, role }),
  }).then(handleResponse<TokenResponse>);
}

export function login(username: string, password: string): Promise<TokenResponse> {
  return fetch(`${EVENT_SERVICE_URL}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  }).then(handleResponse<TokenResponse>);
}

export function fetchEvents(status?: EventStatus): Promise<Event[]> {
  const query = status ? `?status=${status}` : "";
  return fetch(`${EVENT_SERVICE_URL}/api/events${query}`).then(handleResponse<Event[]>);
}

export function createEvent(
  token: string,
  input: {
    title: string;
    description: string;
    venue: string;
    startsAt: string;
    totalSeats: number;
  }
): Promise<Event> {
  return fetch(`${EVENT_SERVICE_URL}/api/events`, {
    method: "POST",
    headers: authHeaders(token),
    body: JSON.stringify(input),
  }).then(handleResponse<Event>);
}

export function publishEvent(token: string, id: string): Promise<Event> {
  return fetch(`${EVENT_SERVICE_URL}/api/events/${id}/publish`, {
    method: "PATCH",
    headers: authHeaders(token),
  }).then(handleResponse<Event>);
}

export function cancelEvent(token: string, id: string): Promise<Event> {
  return fetch(`${EVENT_SERVICE_URL}/api/events/${id}/cancel`, {
    method: "PATCH",
    headers: authHeaders(token),
  }).then(handleResponse<Event>);
}

export function bookSeat(token: string, eventId: string): Promise<Booking> {
  return fetch(`${BOOKING_SERVICE_URL}/api/bookings`, {
    method: "POST",
    headers: authHeaders(token),
    body: JSON.stringify({ eventId }),
  }).then(handleResponse<Booking>);
}

export function fetchAvailability(eventId: string): Promise<Availability> {
  return fetch(`${BOOKING_SERVICE_URL}/api/events/${eventId}/availability`).then(
    handleResponse<Availability>
  );
}
