import { useState, useMemo, useEffect } from "react";
import "./Dashboard.css";

const COLORS = ["#3b6ef6", "#818cf8", "#34d399", "#fbbf24", "#48cae4"];
const AV_CLS = ["av-blue", "av-purple", "av-green", "av-gold", "av-coral"];
const RANK_CLS = { EXPERT: "rank-expert", PRO: "rank-pro", JUNIOR: "rank-junior" };

function initials(name) {
  return name.substring(0, 2).toUpperCase();
}

/* ══ DONUT ══ */
function Donut({ members }) {
  const levels = [
    { label: "EXPERT", color: "#34d399" },
    { label: "PRO",    color: "#3b6ef6" },
    { label: "JUNIOR", color: "#818cf8" },
  ];
  const counts = levels.map((l) => ({
    ...l,
    n: members.filter((m) => m.rank === l.label).length,
  }));
  const total = counts.reduce((s, d) => s + d.n, 0) || 1;
  const r = 40, cx = 55, cy = 55, sw = 10, circ = 2 * Math.PI * r;
  let off = 0;
  const segs = counts.map((d) => {
    const dash = (d.n / total) * circ;
    const s = { ...d, dash, off };
    off += dash;
    return s;
  });

  return (
    <div className="donut-wrap">
      <svg className="donut-svg" viewBox="0 0 110 110">
        <circle cx={cx} cy={cy} r={r} fill="none" stroke="#112240" strokeWidth={sw} />
        {segs.filter((s) => s.n > 0).map((s, i) => (
          <circle key={i} cx={cx} cy={cy} r={r} fill="none"
            stroke={s.color} strokeWidth={sw}
            strokeDasharray={`${s.dash} ${circ - s.dash}`}
            strokeDashoffset={-s.off + circ / 4}
            strokeLinecap="round"
          />
        ))}
        <text x={cx} y={cy - 4} textAnchor="middle" fill="#f8fafc"
          fontSize="18" fontWeight="800" fontFamily="Outfit">{total}</text>
        <text x={cx} y={cy + 14} textAnchor="middle" fill="#64748b"
          fontSize="9" fontFamily="Outfit">membres</text>
      </svg>
      <div className="donut-legend">
        {counts.map((d, i) => (
          <div className="dleg" key={i}>
            <div className="dleg-left">
              <div className="dleg-dot" style={{ background: d.color }} />
              <span className="dleg-name">{d.label}</span>
            </div>
            <span className="dleg-val">{d.n}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

/* ══ BAR CHART ══ */
function BarChart({ members }) {
  const max = Math.max(...members.map((m) => m.commitCount), 1);
  const H = 72;
  return (
    <div className="bar-chart">
      {members.map((m, i) => {
        const h = Math.round((m.commitCount / max) * H);
        return (
          <div className="bcol" key={i}>
            <span className="bv">{m.commitCount}</span>
            <div className="bar-fill" style={{ height: h, background: COLORS[i % COLORS.length] }} />
            <span className="bname">{m.author.substring(0, 6)}</span>
          </div>
        );
      })}
    </div>
  );
}

/* ══ TREND CHART ══ */
function TrendChart({ member, members }) {
  const hist = member?.history;
  if (!hist || hist.length === 0) return <div className="no-data">Pas de données</div>;
  const W = 240, H = 80, PL = 8, PR = 8, PT = 8, PB = 8;
  const iW = W - PL - PR, iH = H - PT - PB;
  const vals = hist.map((d) => d.dailyScore);
  const mn = Math.min(...vals), mx = Math.max(...vals);
  const range = mx - mn || 1;
  const coords = hist.map((d, i) => ({
    x: PL + (i / (hist.length > 1 ? hist.length - 1 : 1)) * iW,
    y: PT + iH - ((d.dailyScore - mn) / range) * iH,
    ...d,
  }));
  const poly = coords.map((c) => `${c.x},${c.y}`).join(" ");
  const area = `${coords[0].x},${H} ` + poly + ` ${coords[coords.length - 1].x},${H}`;
  const ci = members.indexOf(member);
  const col = COLORS[ci % COLORS.length];
  const gradId = `tg-${ci}`;

  return (
    <>
      <svg className="trend-svg" viewBox={`0 0 ${W} ${H}`} style={{ height: 90, overflow: "visible" }}>
        <defs>
          <linearGradient id={gradId} x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%"   stopColor={col} stopOpacity="0.25" />
            <stop offset="100%" stopColor={col} stopOpacity="0" />
          </linearGradient>
        </defs>
        <polygon points={area} fill={`url(#${gradId})`} />
        <polyline points={poly} fill="none" stroke={col} strokeWidth="2"
          strokeLinecap="round" strokeLinejoin="round" />
        {coords.map((c, i) => (
          <circle key={i} cx={c.x} cy={c.y} r="3" fill={col} stroke="#07111f" strokeWidth="1.5" />
        ))}
      </svg>
      <div className="act-list">
        {[...hist].reverse().map((h, i) => (
          <div className="act-item" key={i}>
            <div className="act-dot" style={{ background: col }} />
            <div className="act-body">
              <div className="act-date">{h.date}</div>
              <div className="act-lines">+{h.linesAdded.toLocaleString()} lignes</div>
            </div>
            <div className="act-pts" style={{ color: col }}>{Math.round(h.dailyScore)} pts</div>
          </div>
        ))}
      </div>
    </>
  );
}

/* ══ STAT CARD ══ */
function StatCard({ label, value, color, change, pct, icon, barColor }) {
  return (
    <div className="scard">
      <div className="scard-top">
        <div className={`scard-icon ${color}`}>
          <svg width="17" height="17" viewBox="0 0 24 24" fill="none" strokeWidth="2" stroke="currentColor">
            {icon}
          </svg>
        </div>
        <span className="scard-change up">{change}</span>
      </div>
      <div className="scard-val">{value}</div>
      <div className="scard-label">{label}</div>
      <div className="scard-bar">
        <div className="scard-bar-fill" style={{ width: `${pct}%`, background: barColor }} />
      </div>
    </div>
  );
}

/* ══ MAIN DASHBOARD ══ */
export default function Dashboard() {
  const [members, setMembers] = useState([]);
  const [selected, setSelected] = useState(null);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("http://localhost:8080/api/performance/leaderboard")
      .then((res) => res.json())
      .then((data) => {
        setMembers(data);
        setSelected(data[0] || null);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Erreur Backend:", err);
        setLoading(false);
      });
  }, []);

  const filtered = useMemo(
    () => members.filter((m) => m.author.toLowerCase().includes(search.toLowerCase())),
    [search, members]
  );

  if (loading) return <div className="app-loading">Chargement...</div>;
  if (!members.length) return <div className="app-loading">Aucune donnée disponible.</div>;

  const totalCommits = members.reduce((s, m) => s + m.commitCount, 0);
  const totalLines   = members.reduce((s, m) => s + m.linesAdded, 0);
  const avgScore     = (members.reduce((s, m) => s + m.score, 0) / members.length).toFixed(1);

  const m  = selected || members[0];
  const ci = members.indexOf(m);
  const rc = RANK_CLS[m.rank] || "rank-junior";

  return (
    <div className="app">

      {/* ── TOPBAR ── */}
      <div className="topbar">
        <div className="topbar-brand">
          <div className="brand-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="2">
              <path d="M9 19c-5 1.5-5-2.5-7-3m14 6v-3.87a3.37 3.37 0 0 0-.94-2.61c3.14-.35 6.44-1.54 6.44-7A5.44 5.44 0 0 0 20 4.77 5.07 5.07 0 0 0 19.91 1S18.73.65 16 2.48a13.38 13.38 0 0 0-7 0C6.27.65 5.09 1 5.09 1A5.07 5.07 0 0 0 5 4.77a5.44 5.44 0 0 0-1.5 3.78c0 5.42 3.3 6.61 6.44 7A3.37 3.37 0 0 0 9 18.13V22" />
            </svg>
          </div>
          <div>
            <div className="brand-name">Git Quality</div>
            <div className="brand-sub">Performance Tracker</div>
          </div>
        </div>
        <div className="topbar-right">
          <div className="topbar-badge"><div className="pulse" />Live</div>
          <div className="topbar-date">Last sync: {m.lastCommitDate}</div>
        </div>
      </div>

      <div className="main">

        {/* ── SIDEBAR ── */}
        <div className="sidebar">
          <div className="sidebar-label">Équipe</div>
          <div className="search-box">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="11" cy="11" r="8" /><path d="m21 21-4.35-4.35" />
            </svg>
            <input placeholder="Rechercher un membre..." value={search} onChange={(e) => setSearch(e.target.value)} />
          </div>
          <div className="mlist">
            {filtered.map((mbr) => {
              const idx = members.indexOf(mbr);
              return (
                <div key={mbr.author}
                  className={`mcard ${mbr.author === m.author ? "active" : ""}`}
                  onClick={() => setSelected(mbr)}>
                  <div className="mcard-row">
                    <div className={`avatar ${AV_CLS[idx % AV_CLS.length]}`}>{initials(mbr.author)}</div>
                    <div className="mcard-info">
                      <div className="mcard-name">{mbr.author}</div>
                      <div className="mcard-rank">{mbr.rank} · {mbr.commitCount} commits</div>
                    </div>
                    <div className="mcard-score">{Math.round(mbr.score)}</div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* ── CONTENT ── */}
        <div className="content">
          <div className="content-inner">

            {/* Stats */}
            <div className="stats-grid">
              <StatCard label="Membres" value={members.length} color="blue" barColor={COLORS[0]} change="+0%" pct={100}
                icon={<><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></>} />
              <StatCard label="Total Commits" value={totalCommits} color="purple" barColor={COLORS[1]} change="+12%" pct={72}
                icon={<><circle cx="18" cy="18" r="3"/><circle cx="6" cy="6" r="3"/><path d="M13 6h3a2 2 0 0 1 2 2v7"/><line x1="6" y1="9" x2="6" y2="21"/></>} />
              <StatCard label="Lignes Ajoutées" value={totalLines.toLocaleString()} color="green" barColor={COLORS[2]} change="+34%" pct={85}
                icon={<><polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/></>} />
              <StatCard label="Score Moyen" value={avgScore} color="gold" barColor={COLORS[3]} change="+8%" pct={60}
                icon={<polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>} />
            </div>

            {/* Hero */}
            <div className="hero-panel">
              <div className={`hero-avatar ${AV_CLS[ci % AV_CLS.length]}`}>{initials(m.author)}</div>
              <div className="hero-info">
                <div className="hero-name">{m.author}</div>
                <div className="hero-sub">Dernier commit : {m.lastCommitDate}</div>
                <div className="hero-kpis">
                  <div className="kpi"><div className="kpi-v">{Math.round(m.score)}</div><div className="kpi-l">Score</div></div>
                  <div className="kpi"><div className="kpi-v">{m.commitCount}</div><div className="kpi-l">Commits</div></div>
                  <div className="kpi"><div className="kpi-v">{m.linesAdded.toLocaleString()}</div><div className="kpi-l">Lignes</div></div>
                  <div className="kpi"><div className="kpi-v">{m.filesModified}</div><div className="kpi-l">Fichiers</div></div>
                </div>
              </div>
              <span className={`rank-badge ${rc}`}>{m.rank}</span>
            </div>

            {/* Charts */}
            <div className="detail-grid">
              <div className="panel">
                <div className="panel-title">Niveaux</div>
                <Donut members={members} />
              </div>
              <div className="panel">
                <div className="panel-title">Commits par membre</div>
                <BarChart members={members} />
              </div>
              <div className="panel">
                <div className="panel-title">Tendance d'activité</div>
                <TrendChart member={m} members={members} />
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>
  );
}