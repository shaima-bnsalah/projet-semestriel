import { BrowserRouter, Routes, Route } from "react-router-dom";
import SignIn from "./signin/SignIn";
import Search from "./search/Search";
import SignUp from "./signup/singup";
import Teamdashbord from "./TeamDashbord/teamdashbord";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<SignIn />} />
        <Route path="/signup" element={<SignUp />} />
        <Route path="/search" element={<Search />} />
        <Route path="/dashboard" element={<Teamdashbord />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;