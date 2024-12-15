import * as React from 'react';
import { useNavigate } from 'react-router-dom';

import Buttons from '@components/common/button/Buttons';
import { setAuthToken, setUserRole } from '@util/authService';

import logo from '@assets/images/logo.png';
import './Header.css';

export default function Header() {
  const navigate = useNavigate();

  const logout = () => {
    setAuthToken(null);
    setUserRole(null);
    navigate(0)
  };

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

      <Buttons logout={logout} />
    </header>
  );
};
