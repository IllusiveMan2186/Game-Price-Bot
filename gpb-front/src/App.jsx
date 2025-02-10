import './styles/app.css'

import Router from '@routes/Router';
import Footer from '@components/common/layout/footer/Footer';
import { BrowserRouter } from "react-router-dom";

function App() {
  
  console.log("win "+window._env_);
  return (
    <div className="app">
      <BrowserRouter>
        <div className="row">
          <div className="col">
            <Router />
          </div>
        </div>
        <Footer />
      </BrowserRouter>
    </div>
  );
}

export default App;