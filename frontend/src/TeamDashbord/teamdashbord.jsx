import { useState, useEffect } from "react";
import "./teamdashbord.css";

function getUniqueColor(id) {
  return `hsl(${(id * 137.5) % 360}, 65%, 55%)`;
}

function getBarColor(id) {
  return `hsl(${(id * 137.5) % 360}, 65%, 65%)`;
}

const DATA = [
  { id: 11257728,   initials: "MB", name: "Mariem B.",  commits: 987, erreurs: 7,  perf: "342ms", activite: 78, enLigne: true  },
  { id: 245715,     initials: "SA", name: "Souha A.",   commits: 834, erreurs: 2,  perf: "198ms", activite: 85, enLigne: false },
  { id: 8275183,    initials: "SB", name: "Shaima B.",  commits: 712, erreurs: 11, perf: "412ms", activite: 61, enLigne: true  },
  { id: 7587287635, initials: "TB", name: "Taysir B.",  commits: 601, erreurs: 5,  perf: "261ms", activite: 73, enLigne: false },
];

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

function MemberCard({ member, index }) {
  const [visible, setVisible] = useState(false);

  const color    = getUniqueColor(member.id);
  const barColor = getBarColor(member.id);

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
        </div>
      </div>

      
      <div className="td-stats-row">
        <div className="td-stat-box">
          <span className="td-stat-value">
            {member.commits.toLocaleString("fr-FR")}
          </span>
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

export default function TeamDashboard() {
  const [team, setTeam] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setTeam(DATA);
    setLoading(false);
  }, []);

  return (
    <div className="td-page">
      <div className="td-noise-bg" />

      <div className="td-container">
        <div className="td-title-row">
          <div className="td-title-dot" />
          <h1 className="td-title">Équipe</h1>
          <span className="td-count">{team.length} membres</span>
        </div>

        {loading && <div className="td-state-msg">Chargement…</div>}

        {!loading && (
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