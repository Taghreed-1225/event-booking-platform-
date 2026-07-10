import { useEffect, useState } from "react";
import { cancelEvent, fetchEvents, publishEvent } from "../api/client";
import type { ApiError, Event } from "../types";
import BookSeatButton from "./BookSeatButton";
import { useAuth } from "../auth/AuthContext";

export default function EventList() {
  const { token, role } = useAuth();
  const isAdmin = role === "ADMIN";
  const [events, setEvents] = useState<Event[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  function loadEvents() {
    setLoading(true);
    fetchEvents()
      .then((data) => {
        setEvents(data);
        setError(null);
      })
      .catch((err: ApiError) => setError(err.message))
      .finally(() => setLoading(false));
  }

  useEffect(() => {
    loadEvents();
  }, []);

  async function handlePublish(id: string) {
    if (!token) return;
    try {
      await publishEvent(token, id);
      loadEvents();
    } catch (err) {
      setError((err as ApiError).message);
    }
  }

  async function handleCancel(id: string) {
    if (!token) return;
    try {
      await cancelEvent(token, id);
      loadEvents();
    } catch (err) {
      setError((err as ApiError).message);
    }
  }

  if (loading) return <p className="state-message">Loading events...</p>;
  if (error) return <p className="state-message error">Error: {error}</p>;
  if (events.length === 0) return <p className="state-message">No events yet.</p>;

  return (
    <div>
      {events.map((event) => (
        <div key={event.id} className="event-card">
          <div className="event-card-top">
            <div>
              <h3>{event.title}</h3>
              <p className="event-meta">
                {event.venue} &middot; {new Date(event.startsAt).toLocaleString()} &middot; {event.totalSeats} seats
              </p>
            </div>
            <span className={`badge badge-${event.status}`}>{event.status}</span>
          </div>

          <div className="event-actions">
            {isAdmin && event.status === "DRAFT" && (
              <button className="btn btn-small" onClick={() => handlePublish(event.id)}>
                Publish
              </button>
            )}
            {isAdmin && event.status !== "CANCELLED" && (
              <button className="btn btn-small" onClick={() => handleCancel(event.id)}>
                Cancel
              </button>
            )}
            {event.status === "PUBLISHED" && <BookSeatButton eventId={event.id} />}
          </div>
        </div>
      ))}
    </div>
  );
}
