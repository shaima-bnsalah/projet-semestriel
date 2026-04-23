import { useState } from "react";
import "./Search.css";
import { useNavigate } from "react-router-dom";

const GithubIcon = () => (
    <svg viewBox="0 0 16 16">
      <path d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38
        0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13
        -.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66
        .07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15
        -.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0
        1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82
        1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01
        1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"/>
    </svg>
  );


function SearchIcon() {
  return (
    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="white">
      <circle cx="11" cy="11" r="8" />
      <line x1="21" y1="21" x2="16.65" y2="16.65" />
    </svg>
  );
}

function AlertIcon() {
  return (
    <svg width="13" height="13" viewBox="0 0 24 24" fill="none">
      <circle cx="12" cy="12" r="10" />
      <line x1="12" y1="8" x2="12" y2="12" />
      <line x1="12" y1="16" x2="12.01" y2="16" />
    </svg>
  );
}

export default function Search() {
  const [url, setUrl] = useState("");
  const [focused, setFocused] = useState(false);
  const [error, setError] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

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


      navigate("/dashboard");
    }, 800);
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") handleSearch();
  };

  const inputClass = [
    "gs-input",
    focused ? "gs-input--focus" : "",
    error ? "gs-input--error" : "",
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
            onChange={(e) => {
              setUrl(e.target.value);
              setError("");
              setResult(null);
            }}
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