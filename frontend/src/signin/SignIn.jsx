import { useState } from "react";
import "./SignIn.css";
import img from "./image1.png";
import { useNavigate } from "react-router-dom";

export default function SignIn() {
  const [email, setEmail] = useState("");
  const [pwd, setPwd] = useState("");
  const [showPwd, setShowPwd] = useState(false);
  const [remember, setRemember] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  // ✅ LOGIN
  const handleSubmit = () => {
    setError("");

    if (!email.trim() || !pwd) {
      setError("Veuillez remplir tous les champs.");
      return false;
    }

    if (!email.includes("@")) {
      setError("Adresse email invalide.");
      return false;
    }

    setLoading(true);

    setTimeout(() => {
      setLoading(false);
    }, 1400);

    return true;
  };

  // ✅ GITHUB LOGIN
  const handleGH = () => {
    setLoading(true);

    setTimeout(() => {
      setLoading(false);
    }, 900);
  };

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

  return (
    <div className="gt-login">

      {/* BACKGROUND */}
      <div className="gt-bg">
        <img src={img} alt="" />
      </div>

      {/* CARD */}
      <div className="gt-card">
        <div className="gt-card-inner">

          {/* LOGO */}
          <div className="gt-logo">
            <div className="gt-logo-mark">
              <GithubIcon />
            </div>
            <span className="gt-logo-name">GitTrack</span>
          </div>

          <h1 className="gt-h1">
            Bon retour,<br /><em>bienvenue.</em>
          </h1>
          <p className="gt-sub">Connexion à votre espace de suivi.</p>

          {/* ERROR */}
          {error && <div className="gt-error">{error}</div>}

          {/* FIELDS */}
          <div className="gt-fields">

            {/* EMAIL */}
            <div className="gt-field">
              <input
                className={`gt-input${error ? " error" : ""}`}
                type="email"
                placeholder="votre@email.com"
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value);
                  setError("");
                }}
              />
            </div>

            {/* PASSWORD */}
            <div className="gt-field">
              <input
                className={`gt-input${error ? " error" : ""}`}
                type={showPwd ? "text" : "password"}
                placeholder="Mot de passe"
                value={pwd}
                onChange={(e) => {
                  setPwd(e.target.value);
                  setError("");
                }}
              />

              <span
                className="gt-eye"
                onClick={() => setShowPwd((v) => !v)}
              >
                👁
              </span>
            </div>
          </div>

          {/* CHECKBOX */}
          <div className="gt-row">
            <label>
              <input
                type="checkbox"
                checked={remember}
                onChange={(e) => setRemember(e.target.checked)}
              />
             <span>Rester connecté</span>
          </label>
          <span className="gt-forgot">Mot de passe oublié ?</span>
        </div>

          {/* LOGIN BUTTON */}
          <button
            className="gt-btn"
            onClick={() => {
              const ok = handleSubmit();
              if (ok) navigate("/search");
            }}
            disabled={loading}
          >
            {loading ? (
              <div className="gt-spin" />
            ) : (
              <>
                Se connecter
                <svg viewBox="0 0 16 16">
                  <path d="M3 8h10M9 4l4 4-4 4" />
                </svg>
              </>
            )}
          </button>

          {/* DIVIDER */}
          <div className="gt-div">
            <span>ou</span>
          </div>

          {/* GITHUB */}
          <button
            className="gt-gh"
            onClick={() => {
              handleGH();
              navigate("/search");
            }}
            disabled={loading}
          >
            <GithubIcon /> Continuer avec GitHub
          </button>

          {/* SIGNUP */}
          <div className="gt-foot">
            Pas encore de compte ?{" "}
            <span
              onClick={() => navigate("/signup")}
              style={{ cursor: "pointer", color: "#4f46e5" }}
            >
              Créer un compte
            </span>
          </div>

        </div>
      </div>
    </div>
  );
}