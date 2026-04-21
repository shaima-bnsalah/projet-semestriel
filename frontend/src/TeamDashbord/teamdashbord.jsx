import { useState, useEffect } from "react";
import "./teamdashbord.css";

// ─── COLORS ─────────────────────────────
const ROLE_COLORS = {
  "Lead Dev": { color: "#4f46e5", barColor: "#6366f1" },
  "Backend":  { color: "#0d9488", barColor: "#14b8a6" },
  "Frontend": { color: "#7c3aed", barColor: "#8b5cf6" },
  "DevOps":   { color: "#b45309", barColor: "#f59e0b" },
  "Mobile":   { color: "#9d174d", barColor: "#ec4899" },
  "QA":       { color: "#065f46", barColor: "#10b981" },
};

const DEFAULT_COLOR = { color: "#334155", barColor: "#64748b" };

function getRoleColors(role) {
  return ROLE_COLORS[role] ?? DEFAULT_COLOR;
}

// ─── MOCK DATA ──────────────────────────
const MOCK_DATA = [
  { id: 1, initials: "SK", name: "Sarah K.", role: "Lead Dev", commits: 1243, erreurs: 3, perf: "287ms", activite: 92, enLigne: true },
  { id: 2, initials: "TM", name: "Mariem B.", role: "Backend", commits: 987, erreurs: 7, perf: "342ms", activite: 78, enLigne: true },
  { id: 3, initials: "IB", name: "Souha A.", role: "Frontend", commits: 834, erreurs: 2, perf: "198ms", activite: 85, enLigne: false },
  { id: 4, initials: "AR", name: "Shaima B.", role: "DevOps", commits: 712, erreurs: 11, perf: "412ms", activite: 61, enLigne: true },
  { id: 5, initials: "LP", name: "taysir B.", role: "Mobile", commits: 601, erreurs: 5, perf: "261ms", activite: 73, enLigne: false },
  { id: 6, initials: "ND", name: "Nour D.", role: "QA", commits: 389, erreurs: 1, perf: "224ms", activite: 68, enLigne: true },
];

// ─── ANIMATED BAR ───────────────────────
function AnimatedBar({ value, color, delay = 0 }) {
  const [width, setWidth] = useState(0);

  useEffect(() => {
    const timer = setTimeout(() => setWidth(value), 300 + delay);
    return () => clearTimeout(timer);
  }, [value, delay]);

  return (
    <div className="td-bar-track">
      <div
        className="td-bar-fill"
        style={{ width: `${width}%`, background: color }}
      />
    </div>
  );
}

// ─── MEMBER CARD ────────────────────────
function MemberCard({ member, index }) {
  const [visible, setVisible] = useState(false);
  const { color, barColor } = getRoleColors(member.role);

  useEffect(() => {
    const timer = setTimeout(() => setVisible(true), index * 100);
    return () => clearTimeout(timer);
  }, [index]);

  return (
    <div
      className={`td-card ${visible ? "td-card--visible" : ""}`}
      style={{ borderColor: color + "44" }}
    >
      <div
        className="td-card-accent"
        style={{ background: `linear-gradient(90deg, ${color}, transparent)` }}
      />

      <div className="td-card-header">
        <div
          className="td-avatar"
          style={{
            background: `radial-gradient(circle at 30% 30%, ${color}cc, ${color}55)`,
            boxShadow: `0 0 18px ${color}66`,
          }}
        >
          {member.initials}
        </div>

        <div>
          <div className="td-member-name">{member.name}</div>
          <div className="td-member-role">{member.role}</div>
        </div>
      </div>

      <div className="td-stats-row">
        <div className="td-stat-box">
          <span className="td-stat-value">{member.commits.toLocaleString("fr-FR")}</span>
          <span className="td-stat-label">commits</span>
        </div>

        <div className="td-stat-box">
          <span className={`td-stat-value ${member.erreurs > 8 ? "td-stat-value--danger" : ""}`}>
            {member.erreurs}
          </span>
          <span className="td-stat-label">erreurs</span>
        </div>

        <div className="td-stat-box">
          <span className="td-stat-value">{member.perf}</span>
          <span className="td-stat-label">perf</span>
        </div>
      </div>

      <div className="td-activity-row">
        <span className="td-activity-label">Activité</span>
        <span className="td-activity-pct" style={{ color: barColor }}>
          {member.activite}%
        </span>
      </div>

      <AnimatedBar value={member.activite} color={barColor} delay={index * 100} />

      <div className="td-status-row">
        <div className={`td-status-dot ${member.enLigne ? "td-status-dot--online" : "td-status-dot--offline"}`} />
        <span className={`td-status-text ${member.enLigne ? "td-status-text--online" : "td-status-text--offline"}`}>
          {member.enLigne ? "En ligne" : "Hors ligne"}
        </span>
      </div>
    </div>
  );
}

// ─── DASHBOARD ──────────────────────────
export default function TeamDashboard() {
  const [team, setTeam] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setTeam(MOCK_DATA);
    setLoading(false);
  }, []);

  return (
    <div className="td-page">
      <div className="td-noise-bg" />

      <div className="td-container">
        <div className="td-title-row">
          <div className="td-title-dot" />
          <h1 className="td-title">Équipe</h1>
        </div>

        {loading && <div className="td-state-msg">Chargement…</div>}

        {error && <div className="td-state-msg td-state-msg--error">{error}</div>}

        {!loading && !error && (
          <div className="td-grid">
            {team.map((member, i) => (
              <MemberCard key={member.id} member={member} index={i} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}