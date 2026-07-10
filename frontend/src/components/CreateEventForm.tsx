import { useState, type FormEvent } from "react";
import { createEvent } from "../api/client";
import type { ApiError } from "../types";
import { useAuth } from "../auth/AuthContext";

export default function CreateEventForm({ onCreated }: { onCreated: () => void }) {
  const { token } = useAuth();
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [venue, setVenue] = useState("");
  const [startsAt, setStartsAt] = useState("");
  const [totalSeats, setTotalSeats] = useState(10);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!token) {
      setError("Please sign in first");
      return;
    }
    try {
      await createEvent(token, {
        title,
        description,
        venue,
        startsAt: new Date(startsAt).toISOString(),
        totalSeats,
      });
      setTitle("");
      setDescription("");
      setVenue("");
      setStartsAt("");
      setTotalSeats(10);
      setError(null);
      onCreated();
    } catch (err) {
      setError((err as ApiError).message);
    }
  }

  return (
    <form onSubmit={handleSubmit} className="form">
      <div className="field">
        <label htmlFor="title">Title</label>
        <input
          id="title"
          className="input"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
      </div>

      <div className="field">
        <label htmlFor="description">Description</label>
        <textarea
          id="description"
          className="input"
          rows={3}
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
      </div>

      <div className="field">
        <label htmlFor="venue">Venue</label>
        <input
          id="venue"
          className="input"
          value={venue}
          onChange={(e) => setVenue(e.target.value)}
          required
        />
      </div>

      <div className="field">
        <label htmlFor="startsAt">Starts at</label>
        <input
          id="startsAt"
          type="datetime-local"
          className="input"
          value={startsAt}
          onChange={(e) => setStartsAt(e.target.value)}
          required
        />
      </div>

      <div className="field">
        <label htmlFor="totalSeats">Total seats</label>
        <input
          id="totalSeats"
          type="number"
          min={1}
          className="input"
          value={totalSeats}
          onChange={(e) => setTotalSeats(Number(e.target.value))}
          required
        />
      </div>

      <button type="submit" className="btn btn-primary" disabled={!token}>
        Create event
      </button>
      {!token && <p className="message error">Sign in above first</p>}
      {error && <p className="message error">{error}</p>}
    </form>
  );
}
