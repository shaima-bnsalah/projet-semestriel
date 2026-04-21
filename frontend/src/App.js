import { useState } from "react";
import SignIn from "./signin/SignIn";
import Search from "./search/Search";
import SignUp from "./signup/singup";
import Teamdashbord from "./TeamDashbord/teamdashbord";

function App() {
  const [page, setPage] = useState("signin");

  // SEARCH
  if (page === "search") {
    return (
      <Search
        goToDashboard={() => setPage("dashboard")} 
      />
    );
  }


  if (page === "dashboard") {
    return (
      <Teamdashbord
        goToSearch={() => setPage("search")}
      />
    );
  }

  // SIGNUP
  if (page === "signup") {
    return (
      <SignUp
        onLogin={() => setPage("search")}
        goToSignIn={() => setPage("signin")}
      />
    );
  }

  // SIGNIN (default)
  return (
    <SignIn
      onLogin={() => setPage("search")}
      goToSignUp={() => setPage("signup")}
    />
  );
}

export default App;