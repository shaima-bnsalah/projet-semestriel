import { useState, useMemo } from "react";
import "./Dashboard.css";

/* ══ STATIC DATA ══ */
const ALL_MEMBERS = [
  {
    id: 1,
    email: "shaima@test.com",
    initials: "ST",
    level: "EXPERT",
    score: 616.0,
    commits: 40,
    lines: 1410,
    deletions: 100,
    files: 16,
    lastCommit: "20 Avr 2024",
    topPerformer: true,
    activity: [
      { date: "22 Avr 2026", lines: 491, pts: 74.1 },
      { date: "21 Avr 2026", lines: 210, pts: 50.2 },
      { date: "20 Avr 2026", lines: 180, pts: 40.8 },
    ],
    trend: [
      { day: "16 Avr", pts: 88 },
      { day: "17 Avr", pts: 140 },
      { day: "18 Avr", pts: 160 },
      { day: "19 Avr", pts: 220 },
      { day: "20 Avr", pts: 200 },
      { day: "21 Avr", pts: 265 },
      { day: "22 Avr", pts: 341 },
    ],
    perfParJour: [
      { day: "16 Avr", lines: 180, score: 88 },
      { day: "17 Avr", lines: 200, score: 140 },
      { day: "18 Avr", lines: 360, score: 160 },
      { day: "19 Avr", lines: 120, score: 220 },
      { day: "20 Avr", lines: 290, score: 200 },
      { day: "21 Avr", lines: 620, score: 265 },
      { day: "22 Avr", lines: 900, score: 420 },
    ],
    perf: { scoreTotal: "616.0 pts", scoreMoy: "88.0 pts", commitsJour: "5.7", lignesCommit: "35.25", efficacite: "94.2%" },
  },
  {
    id: 2,
    email: "shaima-bnsalah",
    initials: "SB",
    level: "JUNIOR",
    score: 195.6,
    commits: 5,
    lines: 491,
    deletions: 30,
    files: 8,
    lastCommit: "19 Avr 2026",
    topPerformer: false,
    activity: [
      { date: "19 Avr 2026", lines: 200, pts: 30.1 },
      { date: "18 Avr 2026", lines: 150, pts: 22.4 },
      { date: "17 Avr 2026", lines: 141, pts: 18.2 },
    ],
    trend: [
      { day: "16 Avr", pts: 20 },
      { day: "17 Avr", pts: 35 },
      { day: "18 Avr", pts: 50 },
      { day: "19 Avr", pts: 42 },
      { day: "20 Avr", pts: 60 },
      { day: "21 Avr", pts: 55 },
      { day: "22 Avr", pts: 80 },
    ],
    perfParJour: [
      { day: "16 Avr", lines: 60,  score: 20 },
      { day: "17 Avr", lines: 141, score: 35 },
      { day: "18 Avr", lines: 150, score: 50 },
      { day: "19 Avr", lines: 200, score: 42 },
      { day: "20 Avr", lines: 80,  score: 60 },
      { day: "21 Avr", lines: 100, score: 55 },
      { day: "22 Avr", lines: 120, score: 80 },
    ],
    perf: { scoreTotal: "195.6 pts", scoreMoy: "27.9 pts", commitsJour: "0.7", lignesCommit: "98.2", efficacite: "78.4%" },
  },
];

const DONUT = [
  { label: "EXPERT", n: 1, color: "#22c55e", dot: "g" },
  { label: "JUNIOR", n: 1, color: "#3b82f6", dot: "b" },
];

/* ══ TREND LINE SVG ══ */
function TrendSVG({ color }) {
  const pts = color === "#22c55e"
    ? "0,22 14,15 28,17 42,9 56,11 70,6 84,2"
    : color === "#a855f7"
    ? "0,20 14,13 28,16 42,8 56,10 70,4 84,1"
    : color === "#f59e0b"
    ? "0,18 14,14 28,12 42,7 56,9 70,5 84,2"
    : "0,22 14,16 28,18 42,10 56,12 70,7 84,3";
  return (
    <svg className="scard-trend" width="84" height="28" viewBox="0 0 84 28" fill="none">
      <polyline points={pts} stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

/* ══ DONUT ══ */
function Donut({ data }) {
  const r = 50, cx = 65, cy = 65, sw = 14;
  const circ = 2 * Math.PI * r;
  let off = 0;
  const total = data.reduce((s, d) => s + d.n, 0);
  const segs = data.map(d => {
    const dash = (d.n / total) * circ;
    const s = { ...d, dash, off };
    off += dash;
    return s;
  });
  return (
    <div className="donut-rel">
      <svg className="donut-svg" viewBox="0 0 130 130">
        <circle cx={cx} cy={cy} r={r} fill="none" stroke="#1a2236" strokeWidth={sw} />
        {segs.map((s, i) => (
          <circle key={i} cx={cx} cy={cy} r={r} fill="none"
            stroke={s.color} strokeWidth={sw}
            strokeDasharray={`${s.dash} ${circ - s.dash}`}
            strokeDashoffset={-s.off + circ / 4}
            strokeLinecap="round"
            style={{ filter: `drop-shadow(0 0 5px ${s.color}55)` }}
          />
        ))}
      </svg>
      <div className="donut-center">
        <span className="donut-n">{total}</span>
        <span className="donut-s">Membres</span>
      </div>
    </div>
  );
}

/* ══ BAR CHART ══ */
function BarChart({ members }) {
  const maxC = 50;
  const H = 108;
  return (
    <div className="bar-area">
      <div className="y-axis">
        {[0, 10, 20, 30, 40, 50].map(v => <div key={v} className="ytick">{v}</div>)}
      </div>
      {members.map((m, i) => {
        const h = Math.round((m.commits / maxC) * H);
        return (
          <div className="bcol" key={i}>
            <span className="bval">{m.commits}</span>
            <div className="bar" style={{ height: h }} />
            <span className="bname">{m.email}</span>
          </div>
        );
      })}
    </div>
  );
}

/* ══ TREND LINE ══ */
function TrendChart({ data }) {
  const W = 300, H = 105;
  const PL = 34, PR = 8, PT = 10, PB = 26;
  const iW = W - PL - PR, iH = H - PT - PB;
  const vals = data.map(d => d.pts);
  const mn = Math.min(...vals), mx = Math.max(...vals);
  const range = mx - mn || 1;
  const coords = data.map((d, i) => ({
    x: PL + (i / (data.length - 1)) * iW,
    y: PT + iH - ((d.pts - mn) / range) * iH,
    ...d,
  }));
  const poly = coords.map(c => `${c.x},${c.y}`).join(" ");
  const area = `M${coords[0].x},${PT + iH} ` + coords.map(c => `L${c.x},${c.y}`).join(" ") + ` L${coords[coords.length - 1].x},${PT + iH} Z`;
  const last = coords[coords.length - 1];
  const ticks = [mn, Math.round((mn + mx) / 2), mx];
  return (
    <svg className="line-wrap" viewBox={`0 0 ${W} ${H}`} style={{ overflow: "visible" }}>
      <defs>
        <linearGradient id="lg1" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#3b82f6" stopOpacity=".22" />
          <stop offset="100%" stopColor="#3b82f6" stopOpacity=".01" />
        </linearGradient>
      </defs>
      {ticks.map((v, i) => {
        const y = PT + iH - ((v - mn) / range) * iH;
        return (
          <g key={i}>
            <line x1={PL} y1={y} x2={PL + iW} y2={y} stroke="#1e2a3d" strokeDasharray="3 3" strokeWidth="1" />
            <text x={PL - 4} y={y + 4} textAnchor="end" fill="#334155" fontSize="9" fontFamily="Space Mono">{v}</text>
          </g>
        );
      })}
      <path d={area} fill="url(#lg1)" />
      <polyline points={poly} fill="none" stroke="#3b82f6" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
      {coords.map((c, i) => (
        <circle key={i} cx={c.x} cy={c.y} r="3.5" fill="#3b82f6" stroke="#0d1117" strokeWidth="2" />
      ))}
      {/* tooltip last point */}
      <rect x={last.x - 26} y={last.y - 38} width="56" height="28" rx="6" fill="#1a2236" stroke="#3b82f6" strokeWidth="1" />
      <text x={last.x + 2} y={last.y - 21} textAnchor="middle" fill="#e8edf5" fontSize="9" fontFamily="Space Mono" fontWeight="700">{last.pts} pts</text>
      <text x={last.x + 2} y={last.y - 11} textAnchor="middle" fill="#64748b" fontSize="8" fontFamily="Space Mono">{last.day}</text>
      {/* x labels */}
      {coords.map((c, i) => (
        <text key={i} x={c.x} y={H - 2} textAnchor="middle" fill="#334155" fontSize="8" fontFamily="Space Mono">{c.day}</text>
      ))}
    </svg>
  );
}

/* ══ COMBO CHART ══ */
function ComboChart({ data }) {
  const W = 320, H = 120;
  const PL = 36, PR = 36, PT = 8, PB = 26;
  const iW = W - PL - PR, iH = H - PT - PB;
  const bw = iW / data.length;
  const maxL = Math.max(...data.map(d => d.lines));
  const maxS = Math.max(...data.map(d => d.score));
  const scoreCoords = data.map((d, i) => ({
    x: PL + (i + 0.5) * bw,
    y: PT + iH - (d.score / maxS) * iH,
  }));
  const poly = scoreCoords.map(c => `${c.x},${c.y}`).join(" ");
  const lTicks = [0, Math.round(maxL / 2), maxL];
  const sTicks = [0, Math.round(maxS / 2), maxS];
  return (
    <svg viewBox={`0 0 ${W} ${H}`} style={{ width: "100%", overflow: "visible" }}>
      <defs>
        <linearGradient id="bg2" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stopColor="#22c55e" stopOpacity=".85" />
          <stop offset="100%" stopColor="#22c55e" stopOpacity=".45" />
        </linearGradient>
      </defs>
      {lTicks.map((v, i) => {
        const y = PT + iH - (v / maxL) * iH;
        return <text key={i} x={PL - 4} y={y + 4} textAnchor="end" fill="#334155" fontSize="8" fontFamily="Space Mono">{v}</text>;
      })}
      {sTicks.map((v, i) => {
        const y = PT + iH - (v / maxS) * iH;
        return <text key={i} x={W - PR + 4} y={y + 4} textAnchor="start" fill="#334155" fontSize="8" fontFamily="Space Mono">{v}</text>;
      })}
      {lTicks.map((v, i) => {
        const y = PT + iH - (v / maxL) * iH;
        return <line key={i} x1={PL} y1={y} x2={PL + iW} y2={y} stroke="#1e2a3d" strokeDasharray="3 3" strokeWidth="1" />;
      })}
      {data.map((d, i) => {
        const bh = (d.lines / maxL) * iH;
        return (
          <rect key={i} x={PL + i * bw + bw * 0.18} y={PT + iH - bh}
            width={bw * 0.5} height={bh} rx="3" fill="url(#bg2)" />
        );
      })}
      <polyline points={poly} fill="none" stroke="#3b82f6" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
      {scoreCoords.map((c, i) => (
        <circle key={i} cx={c.x} cy={c.y} r="3" fill="#3b82f6" stroke="#0d1117" strokeWidth="1.5" />
      ))}
      {data.map((d, i) => (
        <text key={i} x={PL + (i + 0.5) * bw} y={H - 2}
          textAnchor="middle" fill="#334155" fontSize="8" fontFamily="Space Mono">{d.day}</text>
      ))}
    </svg>
  );
}

/* ══ MAIN ══ */
export default function Dashboard() {
  const [selId, setSelId] = useState(1);
  const [search, setSearch] = useState("");

  const filtered = useMemo(
    () => ALL_MEMBERS.filter(m => m.email.toLowerCase().includes(search.toLowerCase())),
    [search]
  );

  const m = ALL_MEMBERS.find(x => x.id === selId);

  const STATS = [
    { label: "Total membres",   value: "2",     change: "+12% ce mois", color: "blue",   tc: "#3b82f6",
      icon: <svg viewBox="0 0 24 24" strokeWidth="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg> },
    { label: "Total commits",   value: "45",    change: "+18% ce mois", color: "purple", tc: "#a855f7",
      icon: <svg viewBox="0 0 24 24" strokeWidth="2"><circle cx="12" cy="12" r="4"/><line x1="1.05" y1="12" x2="7" y2="12"/><line x1="17" y1="12" x2="23" y2="12"/></svg> },
    { label: "Lignes ajoutées", value: "1901",  change: "+10% ce mois", color: "green",  tc: "#22c55e",
      icon: <svg viewBox="0 0 24 24" strokeWidth="2"><polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/></svg> },
    { label: "Score moyen",     value: "405.8", change: "+23% ce mois", color: "gold",   tc: "#f59e0b",
      icon: <svg viewBox="0 0 24 24" strokeWidth="2"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/><path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/><path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/><path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/></svg> },
  ];

  const KPIS = [
    { label: "Score",             val: `${m.score} pts`, color: "#f59e0b" },
    { label: "Commits",           val: m.commits,         color: "#a855f7" },
    { label: "Lignes ajoutées",   val: m.lines,           color: "#22c55e" },
    { label: "Lignes supprimées", val: m.deletions,       color: "#ef4444" },
    { label: "Fichiers modifiés", val: m.files,           color: "#3b82f6" },
    { label: "Dernier commit",    val: m.lastCommit,      color: "#64748b" },
  ];

  const PERF = [
    { label: "Score total",        val: m.perf.scoreTotal,   color: "#a855f7" },
    { label: "Score moyen par jour", val: m.perf.scoreMoy,   color: "#3b82f6" },
    { label: "Commits par jour",   val: m.perf.commitsJour,  color: "#f59e0b" },
    { label: "Lignes / commit",    val: m.perf.lignesCommit, color: "#22c55e" },
    { label: "Efficacité",         val: m.perf.efficacite,   color: "#ef4444" },
  ];

  return (
    <div className="app">

      {/* ── Topbar ── */}
      <header className="topbar">
        <div className="topbar-left">
          <div className="gh-logo">
            <svg viewBox="0 0 24 24"><path d="M12 0C5.374 0 0 5.373 0 12c0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23A11.509 11.509 0 0 1 12 5.803c1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.929.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576C20.566 21.797 24 17.3 24 12c0-6.627-5.373-12-12-12z"/></svg>
          </div>
          <h1>GitHub Tracker</h1>
        </div>
        <div className="topbar-right">
          <svg viewBox="0 0 24 24"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          Dernière mise à jour : 22 Avril 2025
          <button className="bell"><svg viewBox="0 0 24 24"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg></button>
        </div>
      </header>

      {/* ── Stat cards ── */}
      <div className="stats-row">
        {STATS.map((s, i) => (
          <div className="scard" key={i}>
            <div className={`scard-icon ${s.color}`}>{s.icon}</div>
            <div className="scard-body">
              <div className="scard-label">{s.label}</div>
              <div className="scard-value">{s.value}</div>
              <div className="scard-change">{s.change}</div>
            </div>
            <TrendSVG color={s.tc} />
          </div>
        ))}
      </div>

      {/* ── Split ── */}
      <div className="body-split">

        {/* Sidebar */}
        <aside className="sidebar">
          <div className="sidebar-title">Membres</div>
          <div className="search">
            <input placeholder="Rechercher un membre..." value={search} onChange={e => setSearch(e.target.value)} />
            <svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          </div>
          <div className="mlist">
            {filtered.map((mbr) => (
              <div key={mbr.id} className={`mcard ${mbr.id === selId ? "active" : ""}`} onClick={() => setSelId(mbr.id)}>
                <div className="mcard-head">
                  <div className={`mcard-avatar ${mbr.level === "EXPERT" ? "exp" : "jun"}`}>{mbr.initials}</div>
                  <div className="mcard-info">
                    <div className="mcard-name">{mbr.email}</div>
                    <span className={`badge ${mbr.level === "EXPERT" ? "exp" : "jun"}`}>{mbr.level}</span>
                  </div>
                  <span className="mcard-arrow">›</span>
                </div>
                <div className="mcard-stats">
                  <div className="mstat"><span className="mstat-label">Score</span><span className="mstat-val">{mbr.score}</span></div>
                  <div className="mstat"><span className="mstat-label">Commits</span><span className="mstat-val">{mbr.commits}</span></div>
                  <div className="mstat"><span className="mstat-label">Lignes ajoutées</span><span className="mstat-val">{mbr.lines}</span></div>
                </div>
              </div>
            ))}
          </div>
          <div className="sidebar-pager">
            <span className="pager-info">1 – {ALL_MEMBERS.length} sur {ALL_MEMBERS.length}</span>
            <div className="pager-btns">
              <button className="pbtn">‹</button>
              <button className="pbtn active">1</button>
              <button className="pbtn">›</button>
            </div>
          </div>
        </aside>

        {/* Detail */}
        <div className="detail">

          {/* Hero card */}
          <div className="hero">
            <div className="hero-top">
              <div className="hero-left">
                <div className={`hero-avatar ${m.level === "EXPERT" ? "exp" : "jun"}`}>{m.initials}</div>
                <div>
                  <div className="hero-name-row">
                    <span className="hero-name">{m.email}</span>
                    <span className={`badge ${m.level === "EXPERT" ? "exp" : "jun"}`}>{m.level}</span>
                  </div>
                  <div className="kpis" style={{ marginTop: 8 }}>
                    {KPIS.map((k, i) => (
                      <div className="kpi" key={i}>
                        <div className="kpi-label" style={{ color: k.color }}>
                          <svg viewBox="0 0 24 24" style={{ stroke: k.color }}><circle cx="12" cy="12" r="4" fill={k.color} stroke="none"/></svg>
                          {k.label}
                        </div>
                        <div className="kpi-val">{k.val}</div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
              {m.topPerformer && (
                <div className="top-perf">
                  <svg viewBox="0 0 24 24"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/><path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/><path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/><path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/></svg>
                  Top Performer
                </div>
              )}
            </div>
            {/* Tabs */}
            <div className="tabs">
              {["Vue d'ensemble", "Commits", "Activité", "Performances"].map((t, i) => (
                <button key={i} className={`tab ${i === 0 ? "active" : ""}`}>{t}</button>
              ))}
            </div>
          </div>

          {/* Charts row */}
          <div className="charts-trio">

            {/* Donut */}
            <div className="ccard">
              <div className="ccard-title">Répartition des niveaux</div>
              <div className="donut-wrap">
                <Donut data={DONUT} />
                <div className="dlegend">
                  {DONUT.map((d, i) => (
                    <div className="dleg" key={i}>
                      <div className="dleg-row">
                        <div className="dleg-dot" style={{ background: d.color, boxShadow: `0 0 5px ${d.color}88` }} />
                        <span className="dleg-name">{d.label} ({d.n})</span>
                      </div>
                      <div className="dleg-pct">50%</div>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Bar chart */}
            <div className="ccard">
              <div className="ccard-title">Commits par membre</div>
              <BarChart members={ALL_MEMBERS} />
            </div>

            {/* Trend */}
            <div className="ccard">
              <div className="ccard-title">Tendance (7 derniers jours)</div>
              <TrendChart data={m.trend} />
            </div>

          </div>

          {/* Bottom row */}
          <div className="bottom-trio">

            {/* Activité */}
            <div className="ccard">
              <div className="ccard-title">Activité récente</div>
              <div className="act-list">
                {m.activity.map((a, i) => (
                  <div className="act-item" key={i}>
                    <div className="act-icon">
                      <svg viewBox="0 0 24 24"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                    </div>
                    <div className="act-body">
                      <div className="act-title">Push – {a.date}</div>
                      <div className="act-sub">+{a.lines} lignes ajoutées</div>
                    </div>
                    <div className="act-pts">{a.pts} pts</div>
                  </div>
                ))}
              </div>
              <button className="link-btn">Voir toute l'activité <span>→</span></button>
            </div>

            {/* Résumé perf */}
            <div className="ccard">
              <div className="ccard-title">Résumé de performance</div>
              <div className="perf-rows">
                {PERF.map((p, i) => (
                  <div className="prow" key={i}>
                    <div className="prow-label" style={{ color: p.color }}>
                      <svg viewBox="0 0 24 24" style={{ stroke: p.color }}><circle cx="12" cy="12" r="9"/></svg>
                      {p.label}
                    </div>
                    <div className="prow-val">{p.val}</div>
                  </div>
                ))}
              </div>
              <button className="link-btn">Voir le détail <span>→</span></button>
            </div>

            {/* Perf par jour */}
            <div className="ccard">
              <div className="ccard-title">Performances par jour</div>
              <div className="combo-legend">
                <div className="cl-item"><div className="cl-dot g" />Lignes ajoutées</div>
                <div className="cl-item"><div className="cl-dot b" />Score</div>
              </div>
              <ComboChart data={m.perfParJour} />
            </div>

          </div>
        </div>
      </div>
    </div>
  );
}