import { useState, useEffect } from "react";
import "./teamdashbord.css";

// ============================================================
// 🔌 API — remplacer par votre appel backend
// ============================================================
// async function fetchTeam() {
//   const res = await fetch("https://votre-api.com/api/stats");
//   if (!res.ok) throw new Error("Erreur API");
//   return res.json();
// }
// ============================================================

// ─── Couleur unique par auteur ─────────────────────────────
function getColor(str) {
  let hash = 0;
  for (let i = 0; i < str.length; i++) hash = str.charCodeAt(i) + ((hash << 5) - hash);
  return `hsl(${Math.abs(hash) % 360}, 65%, 55%)`;
}
function getBarColor(str) {
  let hash = 0;
  for (let i = 0; i < str.length; i++) hash = str.charCodeAt(i) + ((hash << 5) - hash);
  return `hsl(${Math.abs(hash) % 360}, 65%, 65%)`;
}

function getInitials(author) {
  return author
    .replace(/@.*/, "")
    .split(/[-._\s]/)
    .filter(Boolean)
    .map(p => p[0].toUpperCase())
    .slice(0, 2)
    .join("");
}

// ─── Données de démo (format API réel) ────────────────────
const MOCK_DATA = [
  {
    author: "shaima-bnsalah",
    commitCount: 5,
    linesAdded: 491,
    linesDeleted: 170,
    filesModified: 21,
    lastCommitDate: "2026-04-19",
    score: 195.6,
    rank: "JUNIOR",
    enLigne: true,
    history: [
      { date: "2026-04-22", pushes: 1, linesAdded: 491, dailyScore: 74.1 },
    ],
  },
  {
    author: "shaima@test.com",
    commitCount: 40,
    linesAdded: 1410,
    linesDeleted: 100,
    filesModified: 16,
    lastCommitDate: "2024-04-20",
    score: 616.0,
    rank: "EXPERT",
    enLigne: false,
    history: [
      { date: "2026-04-20", pushes: 1, linesAdded: 1410, dailyScore: 341.0 },
    ],
  },
  {
    author: "mariem-bensalah",
    commitCount: 28,
    linesAdded: 980,
    linesDeleted: 240,
    filesModified: 34,
    lastCommitDate: "2026-04-21",
    score: 430.5,
    rank: "SENIOR",
    enLigne: true,
    history: [
      { date: "2026-04-18", pushes: 2, linesAdded: 320, dailyScore: 110.0 },
      { date: "2026-04-21", pushes: 3, linesAdded: 660, dailyScore: 220.5 },
    ],
  },
  {
    author: "taysir-ben",
    commitCount: 14,
    linesAdded: 620,
    linesDeleted: 88,
    filesModified: 11,
    lastCommitDate: "2026-04-20",
    score: 285.0,
    rank: "MID",
    enLigne: true,
    history: [
      { date: "2026-04-19", pushes: 1, linesAdded: 310, dailyScore: 95.0 },
      { date: "2026-04-20", pushes: 2, linesAdded: 310, dailyScore: 95.0 },
    ],
  },
];

// ─── Barre animée ──────────────────────────────────────────
function AnimatedBar({ value, color, delay = 0, className = "td-bar-fill" }) {
  const [width, setWidth] = useState(0);
  useEffect(() => {
    const t = setTimeout(() => setWidth(value), 300 + delay);
    return () => clearTimeout(t);
  }, [value, delay]);
  return <div className={className} style={{ width: `${width}%`, background: color }} />;
}

// ─── Carte membre (grille) ─────────────────────────────────
function MemberCard({ member, index, onClick }) {
  const [visible, setVisible] = useState(false);
  const color    = getColor(member.author);
  const barColor = getBarColor(member.author);
  const initials = getInitials(member.author);
  const activite = Math.min(100, Math.round((member.score / 700) * 100));

  useEffect(() => {
    const t = setTimeout(() => setVisible(true), index * 100);
    return () => clearTimeout(t);
  }, [index]);

  return (
    <div
      className={`td-card ${visible ? "td-card--visible" : ""}`}
      style={{ borderColor: color + "44" }}
      onClick={() => onClick(member)}
    >
      <div className="td-card-accent"
        style={{ background: `linear-gradient(90deg, ${color}, transparent)` }} />

      <div className="td-card-header">
        <div className="td-avatar"
          style={{
            background: `radial-gradient(circle at 30% 30%, ${color}cc, ${color}55)`,
            boxShadow: `0 0 18px ${color}55`,
          }}>
          {initials}
        </div>
        <div>
          <div className="td-member-name">{member.author}</div>
          <div className="td-member-sub">Dernier commit : {member.lastCommitDate}</div>
          <span className={`td-rank td-rank--${member.rank}`}>{member.rank}</span>
        </div>
      </div>

      <div className="td-stats-row">
        <div className="td-stat-box">
          <span className="td-stat-value">{member.commitCount}</span>
          <span className="td-stat-label">commits</span>
        </div>
        <div className="td-stat-box">
          <span className="td-stat-value" style={{ color: "#4ade80" }}>+{member.linesAdded.toLocaleString("fr-FR")}</span>
          <span className="td-stat-label">ajoutées</span>
        </div>
        <div className="td-stat-box">
          <span className="td-stat-value" style={{ color: "#f87171" }}>-{member.linesDeleted.toLocaleString("fr-FR")}</span>
          <span className="td-stat-label">supprimées</span>
        </div>
      </div>

      <div className="td-activity-row">
        <span className="td-activity-label">Score</span>
        <span className="td-activity-pct" style={{ color: barColor }}>{member.score}</span>
      </div>
      <div className="td-bar-track">
        <AnimatedBar value={activite} color={barColor} delay={index * 100} />
      </div>

      <div className="td-status-row">
        <div className={`td-status-dot ${member.enLigne ? "td-status-dot--online" : "td-status-dot--offline"}`} />
        <span className={`td-status-text ${member.enLigne ? "td-status-text--online" : "td-status-text--offline"}`}>
          {member.enLigne ? "En ligne" : "Hors ligne"}
        </span>
      </div>
    </div>
  );
}

// ─── Vue détail membre ─────────────────────────────────────
function MemberDetail({ member, onBack }) {
  const color    = getColor(member.author);
  const barColor = getBarColor(member.author);
  const initials = getInitials(member.author);

  const maxScore = Math.max(...member.history.map(h => h.dailyScore), 1);

  const kpis = [
    { label: "Commits",        val: member.commitCount,                      accent: color },
    { label: "Lignes ajoutées",val: "+" + member.linesAdded.toLocaleString("fr-FR"),   accent: "#4ade80" },
    { label: "Lignes supp.",   val: "-" + member.linesDeleted.toLocaleString("fr-FR"), accent: "#f87171" },
    { label: "Fichiers modif.", val: member.filesModified,                    accent: barColor },
  ];

  return (
    <div className="td-detail">
      <button className="td-back-btn" onClick={onBack}>← Retour</button>

      {/* Hero */}
      <div className="td-detail-hero">
        <div className="td-detail-hero-accent"
          style={{ background: `linear-gradient(90deg, ${color}, transparent)` }} />

        <div className="td-avatar td-avatar--lg"
          style={{
            background: `radial-gradient(circle at 30% 30%, ${color}cc, ${color}55)`,
            boxShadow: `0 0 24px ${color}66`,
          }}>
          {initials}
        </div>

        <div className="td-detail-hero-info">
          <div className="td-detail-name">{member.author}</div>
          <div className="td-detail-author">Dernier commit : {member.lastCommitDate}</div>
          <span className={`td-rank td-rank--${member.rank}`}>{member.rank}</span>
        </div>

        <div className="td-detail-score-block">
          <div className="td-detail-score-val" style={{ color: barColor }}>
            {member.score}
          </div>
          <div className="td-detail-score-lbl">score total</div>
        </div>
      </div>

      {/* KPIs */}
      <div className="td-kpi-grid">
        {kpis.map((k, i) => (
          <div className="td-kpi-card" key={i}>
            <div className="td-kpi-card-bar" style={{ background: k.accent, width: "100%" }} />
            <div className="td-kpi-val" style={{ color: k.accent }}>{k.val}</div>
            <div className="td-kpi-lbl">{k.label}</div>
          </div>
        ))}
      </div>

      {/* Historique */}
      <div className="td-section-title">Historique des push</div>
      <div className="td-history">
        <div className="td-history-head">
          <span>Date</span>
          <span>Lignes ajoutées</span>
          <span>Pushes</span>
          <span>Score / jour</span>
        </div>
        {member.history.map((h, i) => {
          const pct = Math.round((h.linesAdded / Math.max(...member.history.map(x => x.linesAdded), 1)) * 100);
          return (
            <div className="td-history-row" key={i}>
              <span className="td-history-date">{h.date}</span>
              <div className="td-history-bar-wrap">
                <div className="td-history-bar-bg">
                  <div
                    className="td-history-bar-fill"
                    style={{ width: `${pct}%`, background: barColor }}
                  />
                </div>
                <span className="td-history-lines">+{h.linesAdded.toLocaleString("fr-FR")}</span>
              </div>
              <span className="td-history-lines" style={{ textAlign: "center" }}>{h.pushes}</span>
              <span className="td-history-score" style={{ color: barColor }}>{h.dailyScore}</span>
            </div>
          );
        })}
      </div>
    </div>
  );
}

// ─── Dashboard principal ───────────────────────────────────
export default function TeamDashboard() {
  const [team, setTeam] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selected, setSelected] = useState(null);

  useEffect(() => {
    async function load() {
      try {
        // ============================================================
        // 🔌 API — décommenter quand le backend est prêt :
        // const data = await fetchTeam();
        // setTeam(data);
        // ============================================================

        setTeam(MOCK_DATA);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  return (
    <div className="td-page">
      <div className="td-noise-bg" />
      <div className="td-container">

        {/* Header */}
        <div className="td-header">
          <div className="td-title-row">
            <div className="td-title-dot" />
            <h1 className="td-title">{selected ? "Profil" : "Équipe"}</h1>
            {!selected && (
              <span className="td-count">{team.length} membres</span>
            )}
          </div>
        </div>

        {/* États */}
        {loading && <div className="td-state-msg">Chargement…</div>}
        {error   && <div className="td-state-msg td-state-msg--error">Erreur : {error}</div>}

        {/* Vue grille */}
        {!loading && !error && !selected && (
          <div className="td-grid">
            {team.map((member, i) => (
              <MemberCard
                key={member.author}
                member={member}
                index={i}
                onClick={setSelected}
              />
            ))}
          </div>
        )}

        {/* Vue détail */}
        {!loading && !error && selected && (
          <MemberDetail member={selected} onBack={() => setSelected(null)} />
        )}

      </div>
    </div>
  );
}