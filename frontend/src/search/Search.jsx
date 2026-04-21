import { useState } from "react";
import "./Search.css";

function GithubIcon() {
  return (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
      <path d="M12 0C5.37 0 0 5.37 0 12c0 5.3 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61-.546-1.385-1.335-1.755-1.335-1.755-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.605-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 21.795 24 17.295 24 12c0-6.63-5.37-12-12-12" />
    </svg>
  );
}

function SearchIcon() {
  return (
    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="11" cy="11" r="8" />
      <line x1="21" y1="21" x2="16.65" y2="16.65" />
    </svg>
  );
}

function AlertIcon() {
  return (
    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="12" cy="12" r="10" />
      <line x1="12" y1="8" x2="12" y2="12" />
      <line x1="12" y1="16" x2="12.01" y2="16" />
    </svg>
  );
}
export default function Search({ goToDashboard }) {
  const [url, setUrl] = useState("");
  const [focused, setFocused] = useState(false);
  const [error, setError] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const isValidGithubUrl = (val) =>
    /^https?:\/\/(www\.)?github\.com\/[^/\s]+/.test(val.trim());
const handleSearch = () => {
  setError("");
  setResult(null);

  if (!url.trim()) {
    setError("Veuillez entrer une URL GitHub.");
    return;
  }

  if (!isValidGithubUrl(url)) {
    setError("URL invalide. Ex: https://github.com/");
    return;
  }
  setLoading(true);
  setTimeout(() => {
    setLoading(false);
    setResult(url);
    goToDashboard();
  }, 800);
};
  const handleKeyDown = (e) => {
    if (e.key === "Enter") handleSearch();
  };

  const inputClass = [
    "gs-input",
    focused ? "gs-input--focus" : "",
    error   ? "gs-input--error" : "",
  ].filter(Boolean).join(" ");

  return (
    <div className="gs-page">
      <div className="gs-card">

        <div className="gs-logo">
          <div className="gs-logo-mark">
            <GithubIcon />
          </div>
          <span className="gs-logo-name">GitHub Search</span>
        </div>
        <div className="gs-field">
          <span className="gs-field-icon">
            <GithubIcon />
          </span>
          <input
            className={inputClass}
            type="url"
            placeholder="https://github.com"
            value={url}
            onChange={(e) => { setUrl(e.target.value); setError(""); setResult(null); }}
            onFocus={() => setFocused(true)}
            onBlur={() => setFocused(false)}
            onKeyDown={handleKeyDown}
          />
        </div>
        {error && (
          <div className="gs-error">
            <AlertIcon />
            <span>{error}</span>
          </div>
        )}

        <button
          className={`gs-btn${loading ? " gs-btn--loading" : ""}`}
          onClick={handleSearch}
          disabled={loading}
        >
          {loading ? (
            <span className="gs-spinner" />
          ) : (
            <>
              <SearchIcon />
              Rechercher
            </>
          )}
        </button>

      </div>
    </div>
  );
}