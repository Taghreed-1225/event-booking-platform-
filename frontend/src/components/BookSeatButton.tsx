import { useEffect, useState } from "react";
import { bookSeat, fetchAvailability } from "../api/client";
import type { ApiError, Availability } from "../types";
import { useAuth } from "../auth/AuthContext";

export default function BookSeatButton({ eventId }: { eventId: string }) {
  const { token, username } = useAuth();
  const [message, setMessage] = useState<string | null>(null);
  const [isError, setIsError] = useState(false);
  const [availability, setAvailability] = useState<Availability | null>(null);

  function loadAvailability() {
    fetchAvailability(eventId)
      .then(setAvailability)
      .catch(() => setAvailability(null));
  }

  useEffect(() => {
    loadAvailability();
  }, [eventId]);

  const isFull = availability !== null && availability.remainingSeats <= 0;

  async function handleBook() {
    if (!token) {
      setMessage("Sign in above first");
      setIsError(true);
      return;
    }
    try {
      await bookSeat(token, eventId);
      setMessage("Booking confirmed!");
      setIsError(false);
      loadAvailability();
    } catch (err) {
      setMessage((err as ApiError).message);
      setIsError(true);
    }
  }

  return (
    <div>
      {availability && (
        <p className="event-meta">
          {isFull ? "No seats left" : `${availability.remainingSeats} of ${availability.totalSeats} seats left`}
        </p>
      )}
      <div className="book-form">
        <button className="btn btn-primary btn-small" onClick={handleBook} disabled={isFull || !token}>
          {token ? `Book a seat as ${username}` : "Sign in to book"}
        </button>
      </div>
      {message && <p className={`message ${isError ? "error" : "success"}`}>{message}</p>}
    </div>
  );
}
