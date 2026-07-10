import { useState } from "react";
import EventList from "./components/EventList";
import CreateEventForm from "./components/CreateEventForm";
import SignInBar from "./components/SignInBar";
import { AuthProvider, useAuth } from "./auth/AuthContext";
import "./App.css";

type View = "list" | "create";

function AppContent() {
  const { role } = useAuth();
  const [view, setView] = useState<View>("list");
  const [refreshKey, setRefreshKey] = useState(0);

  function handleCreated() {
    setView("list");
    setRefreshKey((k) => k + 1);
  }

  return (
    <div className="page">
      <header className="page-header">
        <h1>Event Booking Platform</h1>
        <p>Create events, publish them, and let attendees book seats.</p>
      </header>

      <SignInBar />

      <nav className="nav">
        <button className="btn" onClick={() => setView("list")} disabled={view === "list"}>
          All Events
        </button>
        {role === "ADMIN" && (
          <button className="btn" onClick={() => setView("create")} disabled={view === "create"}>
            Create Event
          </button>
        )}
      </nav>

      {view === "list" && <EventList key={refreshKey} />}
      {view === "create" && role === "ADMIN" && <CreateEventForm onCreated={handleCreated} />}
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}
