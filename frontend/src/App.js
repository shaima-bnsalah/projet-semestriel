import { useState } from "react";
import SignIn from "./SignIn";
import Search from "./Search";
 
function App() {
  const [page, setPage] = useState("signin");
 
  if (page === "search") {
    return <Search />;
  }
 
  return <SignIn onLogin={() => setPage("search")} />;
}
 
export default App;