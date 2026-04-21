import { useState } from "react";
import "./signup.css";
import img from "./image1.png";

export default function SignUp({ onLogin }) {
  const [email, setEmail] = useState("");
  const [pwd, setPwd] = useState("");
  const [confirmPwd, setConfirmPwd] = useState("");
  const [showPwd, setShowPwd] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [step, setStep] = useState("form");
  const [code, setCode] = useState("");

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

  const handleSubmit = () => {
    setError("");

    if (!email.trim() || !pwd || !confirmPwd)
      return setError("Veuillez remplir tous les champs.");

    if (!email.includes("@"))
      return setError("Adresse email invalide.");

    if (pwd !== confirmPwd)
      return setError("Les mots de passe ne sont pas identiques.");

    setLoading(true);

    setTimeout(() => {
      setLoading(false);
      setStep("verify"); 
    }, 1200);
  };

  const handleVerify = () => {
    if (!code) return setError("Entrer le code");

    setLoading(true);

    setTimeout(() => {
      setLoading(false);
      onLogin?.(); 
    }, 1000);
  };

  return (
    <div className="gt-login">

      <div className="gt-bg">
        <img src={img} alt="" />
      </div>

      <div className="gt-card">
        <div className="gt-card-inner">

          <div className="gt-logo">
            <div className="gt-logo-mark"><GithubIcon /></div>
            <span className="gt-logo-name">GitTrack</span>
          </div>

          <h1 className="gt-h1">Créer un <em>compte</em></h1>
          <p className="gt-sub">Rejoignez votre espace de suivi.</p>

          {error && <div className="gt-error">{error}</div>}

     
          {step === "form" && (
            <>
              <div className="gt-fields">

                {/* EMAIL */}
                <div className="gt-field">
                  <input
                    className="gt-input"
                    type="email"
                    placeholder="votre@email.com"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                  />
                </div>

                <div className="gt-field">
                  <input
                    className="gt-input"
                    type={showPwd ? "text" : "password"}
                    placeholder="Mot de passe"
                    value={pwd}
                    onChange={e => setPwd(e.target.value)}
                  />
                </div>

               
                <div className="gt-field">
                  <input
                    className="gt-input"
                    type={showPwd ? "text" : "password"}
                    placeholder="Confirmer mot de passe"
                    value={confirmPwd}
                    onChange={e => setConfirmPwd(e.target.value)}
                  />

                  <span className="gt-eye" onClick={() => setShowPwd(v => !v)}>
                    👁
                  </span>
                </div>

              </div>

              <button className="gt-btn" onClick={handleSubmit} disabled={loading}>
                {loading ? <div className="gt-spin" /> : "Créer un compte"}
              </button>
            </>
          )}

   
          {step === "verify" && (
            <div className="gt-fields">

              <p className="gt-sub">
                Code envoyé à <b>{email}</b>
              </p>

              <input
                className="gt-input"
                placeholder="Entrer le code"
                value={code}
                onChange={e => setCode(e.target.value)}
              />

              <button className="gt-btn" onClick={handleVerify} disabled={loading}>
                {loading ? <div className="gt-spin" /> : "Vérifier"}
              </button>

            </div>
          )}

          <button className="gt-gh">
            <GithubIcon /> Continuer avec GitHub
          </button>

        </div>
      </div>

    </div>
  );
}