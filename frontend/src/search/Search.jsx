import { useState, useEffect } from "react";
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
    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <circle cx="12" cy="12" r="10" />
      <line x1="12" y1="8" x2="12" y2="12" />
      <line x1="12" y1="16" x2="12.01" y2="16" />
    </svg>
  );
}

function ClockIcon() {
  return (
    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
      <circle cx="12" cy="12" r="10" />
      <polyline points="12 6 12 12 16 14" />
    </svg>
  );
}

const HISTORY_KEY = "git_search_history";
const MAX_HISTORY = 10;

export default function Search() {
  const [url, setUrl] = useState("");
  const [focused, setFocused] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [history, setHistory] = useState([]);
  const [showHistory, setShowHistory] = useState(false);

  const navigate = useNavigate();

  // Charger l'historique depuis localStorage au démarrage
  useEffect(() => {
    const saved = localStorage.getItem(HISTORY_KEY);
    if (saved) setHistory(JSON.parse(saved));
  }, []);

  const saveToHistory = (val) => {
    const updated = [val, ...history.filter(h => h !== val)].slice(0, MAX_HISTORY);
    setHistory(updated);
    localStorage.setItem(HISTORY_KEY, JSON.stringify(updated));
  };

  const removeFromHistory = (val, e) => {
    e.stopPropagation();
    const updated = history.filter(h => h !== val);
    setHistory(updated);
    localStorage.setItem(HISTORY_KEY, JSON.stringify(updated));
  };

  const clearHistory = () => {
    setHistory([]);
    localStorage.removeItem(HISTORY_KEY);
  };

  const isValidInput = (val) =>
    val.trim().startsWith("http") || val.trim().includes(":/") || val.trim().includes(".git");

  const handleSearch = async (searchUrl) => {
    const target = searchUrl || url;
    setError("");

    if (!target.trim()) {
      setError("Veuillez entrer une URL ou un chemin.");
      return;
    }

    if (!isValidInput(target)) {
      setError("Format invalide. Entrez une URL GitHub ou un chemin local .git");
      return;
    }

    setLoading(true);
    setShowHistory(false);

    try {
      const response = await fetch(`http://localhost:8080/api/performance/analyze-local?path=${encodeURIComponent(target.trim())}`, {
        method: "POST"
      });

      if (response.ok) {
        saveToHistory(target.trim());
        navigate("/dashboard");
      } else {
        const msg = await response.text();
        setError("Erreur d'analyse : " + msg);
      }
    } catch (err) {
      setError("Le serveur backend est injoignable.");
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") handleSearch();
    if (e.key === "Escape") setShowHistory(false);
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
          <div className="gs-logo-mark"><GithubIcon /></div>
          <span className="gs-logo-name">GitHub Search</span>
        </div>

        {/* INPUT + HISTORIQUE */}
        <div style={{ position: "relative" }}>
          <div className="gs-field">
            <span className="gs-field-icon"><GithubIcon /></span>
            <input
              className={inputClass}
              type="text"
              placeholder="URL GitHub ou chemin local .git"
              value={url}
              onChange={(e) => { setUrl(e.target.value); setError(""); }}
              onFocus={() => { setFocused(true); setShowHistory(true); }}
              onBlur={() => { setFocused(false); setTimeout(() => setShowHistory(false), 200); }}
              onKeyDown={handleKeyDown}
            />
          </div>

          {/* DROPDOWN HISTORIQUE */}
          {showHistory && history.length > 0 && (
            <div style={{
              position: "absolute", top: "100%", left: 0, right: 0, zIndex: 100,
              background: "#0d1b2e", border: "1px solid #1e3a5f", borderRadius: "10px",
              marginTop: "6px", overflow: "hidden", boxShadow: "0 8px 32px rgba(0,0,0,.4)"
            }}>
              <div style={{
                display: "flex", justifyContent: "space-between", alignItems: "center",
                padding: "10px 14px", borderBottom: "1px solid #1e3a5f"
              }}>
                <span style={{ fontSize: "10px", color: "#4a6582", fontWeight: 700, letterSpacing: "1px", textTransform: "uppercase" }}>
                  Historique récent
                </span>
                <span onClick={clearHistory} style={{
                  fontSize: "10px", color: "#f87171", cursor: "pointer", fontWeight: 600
                }}>
                  Tout effacer
                </span>
              </div>

              {history.map((h, i) => (
                <div key={i} onClick={() => { setUrl(h); handleSearch(h); }}
                  style={{
                    display: "flex", alignItems: "center", gap: "10px",
                    padding: "10px 14px", cursor: "pointer", transition: "background .15s",
                    borderBottom: i < history.length - 1 ? "1px solid #112240" : "none"
                  }}
                  onMouseEnter={e => e.currentTarget.style.background = "#112240"}
                  onMouseLeave={e => e.currentTarget.style.background = "transparent"}
                >
                  <span style={{ color: "#4a6582", flexShrink: 0 }}><ClockIcon /></span>
                  <span style={{ flex: 1, fontSize: "12px", color: "#8faac8", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                    {h}
                  </span>
                  <span onClick={(e) => removeFromHistory(h, e)} style={{
                    color: "#4a6582", fontSize: "14px", cursor: "pointer", flexShrink: 0,
                    fontWeight: 700, lineHeight: 1
                  }}>×</span>
                </div>
              ))}
            </div>
          )}
        </div>

        {error && (
          <div className="gs-error">
            <AlertIcon />
            <span>{error}</span>
          </div>
        )}

        <button
          className={`gs-btn${loading ? " gs-btn--loading" : ""}`}
          onClick={() => handleSearch()}
          disabled={loading}
        >
          {loading ? <span className="gs-spinner" /> : <><SearchIcon /> Rechercher</>}
        </button>

        {/* HISTORIQUE EN BAS SI PAS DE DROPDOWN */}
        {!showHistory && history.length > 0 && (
          <div style={{ marginTop: "20px" }}>
            <div style={{
              fontSize: "10px", color: "#4a6582", fontWeight: 700,
              letterSpacing: "1px", textTransform: "uppercase", marginBottom: "10px"
            }}>
              Recherches récentes
            </div>
            {history.slice(0, 5).map((h, i) => (
              <div key={i} onClick={() => { setUrl(h); handleSearch(h); }}
                style={{
                  display: "flex", alignItems: "center", gap: "10px",
                  padding: "8px 12px", cursor: "pointer", borderRadius: "8px",
                  background: "#0d1b2e", border: "1px solid #1e3a5f",
                  marginBottom: "6px", transition: "border-color .15s"
                }}
                onMouseEnter={e => e.currentTarget.style.borderColor = "#3b6ef6"}
                onMouseLeave={e => e.currentTarget.style.borderColor = "#1e3a5f"}
              >
                <span style={{ color: "#4a6582" }}><ClockIcon /></span>
                <span style={{ flex: 1, fontSize: "12px", color: "#8faac8", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                  {h}
                </span>
                <span onClick={(e) => removeFromHistory(h, e)} style={{
                  color: "#4a6582", fontSize: "16px", cursor: "pointer", fontWeight: 700
                }}>×</span>
              </div>
            ))}
          </div>
        )}

      </div>
    </div>
  );
}