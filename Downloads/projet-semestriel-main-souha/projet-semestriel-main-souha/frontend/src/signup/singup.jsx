import { useState } from "react";
import "./signup.css";
import img from "./image1.png";
import { useNavigate } from "react-router-dom";

export default function SignUp() {
  const [email, setEmail] = useState("");
  const [pwd, setPwd] = useState("");
  const [confirmPwd, setConfirmPwd] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [step, setStep] = useState("form");
  const [code, setCode] = useState("");

  const navigate = useNavigate();

  const GithubIcon = () => (
    <svg viewBox="0 0 16 16" width="20">
      <path fill="white" d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"/>
    </svg>
  );

  // ✅ ÉTAPE 1 : INSCRIPTION
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (!email.trim() || !pwd || !confirmPwd) return setError("Veuillez remplir tous les champs.");
    if (!email.includes("@")) return setError("Adresse email invalide.");
    if (pwd !== confirmPwd) return setError("Les mots de passe ne sont pas identiques.");

    setLoading(true);
    try {
      const response = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          username: email.split('@')[0],
          email: email,
          password: pwd
        })
      });

      if (response.ok) {
        setStep("verify"); // Passe au champ "Code"
      } else {
        const data = await response.json();
        setError(data.message || "Erreur lors de l'inscription");
      }
    } catch (err) {
      setError("Le serveur ne répond pas. Vérifiez que le Backend est allumé.");
    } finally {
      setLoading(false);
    }
  };

  // ✅ ÉTAPE 2 : VÉRIFICATION CODE OTP
  const handleVerify = async (e) => {
    e.preventDefault();
    if (!code) return setError("Veuillez entrer le code de vérification.");

    setLoading(true);
    try {
      const response = await fetch("http://localhost:8080/api/auth/verify-email", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: email, code: code })
      });

      if (response.ok) {
        alert("Compte vérifié avec succès !");
        navigate("/signin"); // Redirige vers la page de connexion
      } else {
        setError("Code de vérification incorrect ou expiré.");
      }
    } catch (err) {
      setError("Erreur de connexion lors de la vérification.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="gt-login">
      <div className="gt-bg"><img src={img} alt="Background" /></div>
      <div className="gt-card">
        <div className="gt-card-inner">
          <div className="gt-logo">
            <div className="gt-logo-mark"><GithubIcon /></div>
            <span className="gt-logo-name">GitTrack</span>
          </div>

          <h1 className="gt-h1">Créer un <em>compte</em></h1>
          <p className="gt-sub">Rejoignez votre espace de suivi.</p>

          {error && <div className="gt-error" style={{color: '#ef4444', background: 'rgba(239, 68, 68, 0.1)', padding: '10px', borderRadius: '8px', marginBottom: '15px', fontSize: '12px'}}>{error}</div>}

          {step === "form" ? (
            <form onSubmit={handleSubmit} className="gt-fields">
              <input
                className="gt-input"
                type="email"
                placeholder="votre@email.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
              <input
                className="gt-input"
                type="password"
                placeholder="Mot de passe"
                value={pwd}
                onChange={(e) => setPwd(e.target.value)}
              />
              <input
                className="gt-input"
                type="password"
                placeholder="Confirmer mot de passe"
                value={confirmPwd}
                onChange={(e) => setConfirmPwd(e.target.value)}
              />
              <button className="gt-btn" type="submit" disabled={loading}>
                {loading ? "Chargement..." : "Créer un compte"}
              </button>
            </form>
          ) : (
            <form onSubmit={handleVerify} className="gt-fields">
              <p className="gt-sub">Un code a été envoyé à <b>{email}</b></p>
              <input
                className="gt-input"
                placeholder="Entrer le code à 6 chiffres"
                value={code}
                onChange={(e) => setCode(e.target.value)}
                maxLength="6"
              />
              <button className="gt-btn" type="submit" disabled={loading}>
                {loading ? "Vérification..." : "Vérifier le code"}
              </button>
            </form>
          )}

          <div className="gt-foot" style={{marginTop: '20px', textAlign: 'center', fontSize: '12px'}}>
            Déjà inscrit ? <span onClick={() => navigate("/signin")} style={{color: '#3b82f6', cursor: 'pointer', fontWeight: '600'}}>Se connecter</span>
          </div>
        </div>
      </div>
    </div>
  );
}