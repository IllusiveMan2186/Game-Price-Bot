import './styles/app.css'

import Router from '@routes/Router';
import Footer from '@components/common/layout/footer/Footer';
import { BrowserRouter } from "react-router-dom";
import { AuthProvider } from "@contexts/AuthContext";
import { RefreshProvider } from "@contexts/RefreshContext";
import { NavigationProvider } from "@contexts/NavigationContext";
import { NotificationContainer } from 'react-notifications';

import { Provider } from 'react-redux';
import { paramsStore } from '@store/store';

function App() {
  return (
    <div className="app">
      <Provider store={paramsStore}>
        <BrowserRouter>
          <NavigationProvider>
            <AuthProvider>
              <RefreshProvider>
                <div className="notification-wrapper">
                  <NotificationContainer />
                </div>

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
      </Provider>
    </div>
  );
}

export default App;