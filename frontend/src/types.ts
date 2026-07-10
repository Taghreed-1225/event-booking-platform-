export type EventStatus = "DRAFT" | "PUBLISHED" | "CANCELLED";

export type UserRole = "ADMIN" | "USER";

export interface Event {
  id: string;
  title: string;
  description: string;
  venue: string;
  startsAt: string;
  totalSeats: number;
  status: EventStatus;
}

export interface Booking {
  id: string;
  eventId: string;
  attendeeName: string;
  bookedAt: string;
}

export interface Availability {
  eventId: string;
  totalSeats: number;
  bookedSeats: number;
  remainingSeats: number;
}

export interface ApiError {
  status: number;
  errorCode: string;
  message: string;
}

export interface TokenResponse {
  token: string;
  username: string;
  role: UserRole;
}
