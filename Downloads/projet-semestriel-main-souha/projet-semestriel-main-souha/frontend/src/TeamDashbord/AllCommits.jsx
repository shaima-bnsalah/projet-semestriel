import { useState, useMemo } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./Dashboard.css";

const AV_CLS = ["av-blue", "av-purple", "av-green", "av-gold", "av-coral"];
function initials(name) { return name ? name.substring(0, 2).toUpperCase() : "??"; }

export default function AllCommits() {
  const location = useLocation();
  const navigate = useNavigate();
  const members = location.state?.members || [];

  const [search, setSearch] = useState("");
  const [dateFrom, setDateFrom] = useState("");
  const [dateTo, setDateTo] = useState("");
  const [filterMember, setFilterMember] = useState("");

  const commits = useMemo(() => {
    return members.flatMap(m =>
      (m.history || []).flatMap(h =>
        (h.commitMessages || [`Update ${h.date}`]).map(msg => ({
          author: m.author,
          date: h.date,
          linesAdded: Math.round(h.linesAdded / (h.commitMessages?.length || 1)),
          linesDeleted: h.linesDeleted || 0,
          score: h.dailyScore,
          message: msg,
          repo: m.branchName || "main"
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
    <div className="app">
      <div className="topbar">
        <div className="topbar-brand">
          <button onClick={() => navigate(-1)} style={{
            background: "rgba(59,110,246,.15)", border: "1px solid rgba(59,110,246,.3)",
            color: "#5a88ff", padding: "8px 16px", borderRadius: "8px",
            cursor: "pointer", fontSize: "13px", marginRight: "16px"
          }}>← Retour</button>
          <div><div className="brand-name">Tous les Commits</div><div className="brand-sub">{commits.length} commits trouvés</div></div>
        </div>
      </div>

      <div style={{ padding: "20px" }}>
        <div className="commits-box">
          <div className="commits-title">TOUS LES COMMITS</div>
          <div className="ct-filters">
            <div className="search-box ct-search">
              <input placeholder="Auteur..." value={search} onChange={e => setSearch(e.target.value)} />
            </div>
            <div className="ct-date-group">
              <input type="date" className="ct-date" value={dateFrom} onChange={e => setDateFrom(e.target.value)} />
            </div>
            <span className="ct-arrow">→</span>
            <div className="ct-date-group">
              <input type="date" className="ct-date" value={dateTo} onChange={e => setDateTo(e.target.value)} />
            </div>
            <div className="ct-date-group">
              <select className="ct-select" value={filterMember} onChange={e => setFilterMember(e.target.value)}>
                <option value="">Tous les membres</option>
                {members.map(m => <option key={m.author} value={m.author}>{m.author}</option>)}
              </select>
            </div>
          </div>

          <table className="ct">
            <thead>
              <tr><th>Auteur</th><th>Message</th><th>Branche</th><th>Lignes +</th><th>Lignes -</th><th>Date</th></tr>
            </thead>
            <tbody>
              {commits.map((c, i) => {
                const idx = members.findIndex(m => m.author === c.author);
                return (
                  <tr key={i}>
                    <td>
                      <div className="ct-author">
                        <div className={`avatar sm ${AV_CLS[idx % AV_CLS.length]}`}>{initials(c.author)}</div>
                        <span>{c.author}</span>
                      </div>
                    </td>
                    <td className="ct-msg">{c.message}</td>
                    <td><span className="ct-repo">{c.repo}</span></td>
                    <td className="ct-add">+{c.linesAdded}</td>
                    <td className="ct-del">-{c.linesDeleted}</td>
                    <td className="ct-date-cell">{c.date}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}