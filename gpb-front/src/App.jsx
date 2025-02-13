import './styles/app.css'

import Router from '@routes/Router';
import Footer from '@components/common/layout/footer/Footer';
import { BrowserRouter } from "react-router-dom";
import { AuthProvider } from "@contexts/AuthContext";
import { RefreshProvider } from "@contexts/RefreshContext";
import { NavigationProvider } from "@contexts/NavigationContext";

function App() {
  return (
    <div className="app">
      <BrowserRouter>
        <NavigationProvider>
          <AuthProvider>
            <RefreshProvider>
              <div className="row">
                <div className="col">
                  <Router />
                </div>
              </div>
              <Footer />
            </RefreshProvider>
          </AuthProvider>
        </NavigationProvider>
      </BrowserRouter>
    </div>
  );
}

export default App;