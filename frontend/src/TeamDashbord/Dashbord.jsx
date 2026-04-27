import { useState, useMemo, useEffect } from "react";
import "./Dashboard.css";

const COLORS = ["#3b6ef6", "#818cf8", "#34d399", "#fbbf24", "#48cae4"];
const AV_CLS = ["av-blue", "av-purple", "av-green", "av-gold", "av-coral"];

function initials(name) { return name ? name.substring(0, 2).toUpperCase() : "??"; }

const GithubLogo = () => (
  <svg viewBox="0 0 16 16" width="20" height="20" fill="white">
    <path d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"/>
  </svg>
);

function CommitsTable({ members }) {
  const [search, setSearch] = useState("");
  const [dateFrom, setDateFrom] = useState("");
  const [dateTo, setDateTo] = useState("");
  const [filterMember, setFilterMember] = useState("");

  const commits = useMemo(() => {
    return members.flatMap(m =>
      (m.history || []).flatMap(h => 
        (h.commitMessages || [`Update ${h.date}`]).map(msg => ({
          author: m.author,
          date: h.date, // Utilise la date réelle de l'historique
          linesAdded: Math.round(h.linesAdded / (h.commitMessages?.length || 1)),
          linesDeleted: h.linesDeleted || 0,
          score: h.dailyScore,
          message: msg
        }))
      )
    ).filter(c => {
      const ms = c.author.toLowerCase().includes(search.toLowerCase());
      const mf = !dateFrom || c.date >= dateFrom;
      const mt = !dateTo || c.date <= dateTo;
      const mm = !filterMember || c.author === filterMember;
      return ms && mf && mt && mm;
    }).sort((a, b) => new Date(b.date) - new Date(a.date));
  }, [members, search, dateFrom, dateTo, filterMember]);

  return (
    <div className="commits-box">
      <div className="commits-title">DERNIERS COMMITS</div>

      <div className="ct-filters">
        <div className="search-box ct-search">
          <input placeholder="Auteur..." value={search} onChange={e=>setSearch(e.target.value)}/>
        </div>
        <div className="ct-date-group">
          <span className="ct-date-label">Du</span>
          <input type="date" className="ct-date" value={dateFrom} onChange={e=>setDateFrom(e.target.value)}/>
        </div>
        <span className="ct-arrow">→</span>
        <div className="ct-date-group">
          <span className="ct-date-label">Au</span>
          <input type="date" className="ct-date" value={dateTo} onChange={e=>setDateTo(e.target.value)}/>
        </div>
        <div className="ct-date-group">
          <select className="ct-select" value={filterMember} onChange={e=>setFilterMember(e.target.value)}>
            <option value="">Tous les membres</option>
            {members.map(m=><option key={m.author} value={m.author}>{m.author}</option>)}
          </select>
        </div>
      </div>

      <table className="ct">
        <thead>
          <tr>
            <th>Auteur</th><th>Message</th>
            <th>Lignes +</th><th>Lignes -</th><th>Date</th><th>Activité</th>
          </tr>
        </thead>
        <tbody>
          {commits.map((c,i) => {
            const idx = members.findIndex(m=>m.author===c.author);
            return (
              <tr key={i}>
                <td>
                  <div className="ct-author">
                    <div className={`avatar sm ${AV_CLS[idx%AV_CLS.length]}`}>{initials(c.author)}</div>
                    <span>{c.author}</span>
                  </div>
                </td>
                <td className="ct-msg">{c.message}</td>
                <td className="ct-add">+{c.linesAdded}</td>
                <td className="ct-del">-{c.linesDeleted}</td>
                <td className="ct-date-cell">{c.date}</td>
                <td><span className={`ct-badge ${c.score > 10 ? "ctb-green" : "ctb-blue"}`}>{c.score > 10 ? "Concentré" : "Réparti"}</span></td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}

function ActivitySummary({ member, members }) {
  if (!member) return null;
  const rank = members.findIndex(x => x.author === member.author) + 1;
  const goalPercent = Math.min(Math.round((member.score / 20) * 100), 100);
  
  return (
    <div className="act-box">
      <div className="act-title">RÉSUMÉ D'ACTIVITÉ</div>
      <div className="act-grid">
        <div className="act-card">
          <div className="act-ico" style={{background:"rgba(59,110,246,.15)"}}>📅</div>
          <div className="act-val">{member.history?.[member.history.length-1]?.commits || 0}</div>
          <div className="act-lbl">Commits session</div>
        </div>
        <div className="act-card">
          <div className="act-ico" style={{background:"rgba(251,191,36,.12)"}}>🏆</div>
          <div className="act-val">#{rank}</div>
          <div className="act-lbl">Classement actuel</div>
        </div>
        <div className="act-card">
          <div className="act-ico" style={{background:"rgba(52,211,153,.12)"}}>🎯</div>
          <div className="act-val">{goalPercent}%</div>
          <div className="act-lbl">Objectif atteint</div>
        </div>
      </div>
    </div>
  );
}

export default function Dashboard() {
  const [members, setMembers] = useState([]);
  const [selected, setSelected] = useState(null);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("http://localhost:8080/api/performance/leaderboard")
      .then(r=>r.json())
      .then(data=>{ setMembers(data); setSelected(data[0]||null); setLoading(false); })
      .catch(()=>setLoading(false));
  }, []);

  const filtered = useMemo(()=>members.filter(m=>m.author.toLowerCase().includes(search.toLowerCase())),[search,members]);

  if (loading) return <div className="app-loading">Analyse en cours...</div>;
  if (!members.length) return <div className="app-loading">Aucune donnée disponible.</div>;

  const totalCommits = members.reduce((s,m)=>s+m.commitCount,0);
  const totalLines   = members.reduce((s,m)=>s+m.linesAdded,0);
  const totalDeleted = members.reduce((s,m)=>s+m.linesDeleted,0);
  const avgScore     = (members.reduce((s,m)=>s+m.score,0)/members.length).toFixed(1);
  const m   = selected||members[0];
  const ci  = members.indexOf(m);

  const scoreQuality = m.score >= 16 ? "Excellent" : m.score >= 10 ? "Très bon" : "Moyen";

  return (
    <div className="app">
      <div className="topbar">
        <div className="topbar-brand">
          <div className="brand-icon"><GithubLogo/></div>
          <div><div className="brand-name">Git Quality</div><div className="brand-sub">Performance Analytics</div></div>
        </div>
      </div>

      <div className="layout">
        <aside className="sidebar">
          <div className="sidebar-label">ÉQUIPE</div>
          <div className="search-box">
            <input placeholder="Chercher..." value={search} onChange={e=>setSearch(e.target.value)}/>
          </div>
          <div className="mlist">
            {filtered.map((mbr, i) => (
              <div key={mbr.author} className={`mcard ${mbr.author===m.author?"active":""}`} onClick={()=>setSelected(mbr)}>
                <div className={`avatar ${AV_CLS[i%AV_CLS.length]}`}>{initials(mbr.author)}</div>
                <div className="mcard-info">
                  <div className="mcard-name">{mbr.author}</div>
                  <div className="mcard-sub">{mbr.rank}</div>
                </div>
                <div className="mcard-lines">{mbr.score}/20</div>
              </div>
            ))}
          </div>
        </aside>

        <main className="content">
          <div className="stats-row">
             {[
              {label:"Membres", val:members.length, icon:"👥", ci:"ico-blue", bar:"#3b6ef6", pct:100},
              {label:"Total Commits", val:totalCommits, icon:"⑂", ci:"ico-purple", bar:"#818cf8", pct:70},
              {label:"Lignes +", val:totalLines.toLocaleString(), icon:"</>", ci:"ico-green", bar:"#34d399", pct:85},
              {label:"Lignes -", val:totalDeleted.toLocaleString(), icon:"—", ci:"ico-red", bar:"#f87171", pct:40},
              {label:"Moyenne / 20", val:avgScore, icon:"★", ci:"ico-gold", bar:"#fbbf24", pct:60},
            ].map((s,i)=>(
              <div className="scard" key={i}>
                <div className="scard-top"><div className={`scard-ico ${s.ci}`}>{s.icon}</div></div>
                <div className="scard-val">{s.val}</div>
                <div className="scard-lbl">{s.label}</div>
                <div className="scard-bar"><div style={{width:`${s.pct}%`,background:s.bar}}/></div>
              </div>
            ))}
          </div>

          <div className="hero">
            <div className={`hero-av ${AV_CLS[ci%AV_CLS.length]}`}>{initials(m.author)}</div>
            <div className="hero-body">
              <div className="hero-name">{m.author}</div>
              <div className="hero-sub">Dernière activité : {m.lastCommitDate}</div>
              <div className="hero-kpis">
                <div className="kpi"><div className="kpi-v">{m.commitCount}</div><div className="kpi-l">COMMITS</div></div>
                <div className="kpi"><div className="kpi-v">+{m.linesAdded}</div><div className="kpi-l">LIGNES</div></div>
                <div className="kpi"><div className="kpi-v">{m.filesModified}</div><div className="kpi-l">FICHIERS</div></div>
              </div>
            </div>
            <div className="hero-score">
              <svg viewBox="0 0 100 100" width="80" height="80">
                <circle cx="50" cy="50" r="38" fill="none" stroke="#112240" strokeWidth="7"/>
                <circle cx="50" cy="50" r="38" fill="none" stroke="#34d399" strokeWidth="7"
                  strokeDasharray={`${(m.score/20)*239} 239`} strokeLinecap="round"/>
                <text x="50" y="45" textAnchor="middle" fill="#fff" fontSize="13" fontWeight="800">{m.score}</text>
                <text x="50" y="58" textAnchor="middle" fill="#64748b" fontSize="7">/ 20</text>
              </svg>
              <div className="hero-qual">{scoreQuality}</div>
            </div>
          </div>

          <div className="bottom-row">
            <CommitsTable members={members}/>
            <ActivitySummary member={m} members={members}/>
          </div>
        </main>
      </div>
    </div>
  );
}