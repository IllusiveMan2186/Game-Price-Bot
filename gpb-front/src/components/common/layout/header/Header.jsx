import * as React from 'react';
import { useNavigate } from 'react-router-dom';

import Buttons from '@components/common/button/Buttons';
import { logout } from '@util/authUtils';
import { areCookiesEnabled } from '@util/cookieUtils';
import { logoutRequest } from '@services/auth';

import logo from '@assets/images/logo.png';
import './Header.css';

export default function Header() {
  const navigate = useNavigate();

  const handleLogout = async () => {
    console.info("logout");
    logout();
  
    if (areCookiesEnabled()) {
      try {
        await logoutRequest();
      } catch (error) {
        console.error("Logout request failed", error);
      }
    }
  
    navigate(0);
  }

  const defaultPage = () => {
    navigate("/")
    navigate(0)
  };

  return (
    <header className="App-header">
      <div className="App-title" onClick={defaultPage}>
        <img src={logo} className="App-logo" alt="logo" />
        <h1 style={{ fontSize: 'auto' }}>GPB</h1>
      </div>

      <Buttons logout={handleLogout} />
    </header>
  );
};