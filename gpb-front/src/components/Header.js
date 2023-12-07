import * as React from 'react';
import Buttons from './Buttons';
import { useNavigate } from "react-router-dom";
import { setAuthHeader, setRoleHeader } from '../helpers/axios_helper';
import logo from '../logo.svg';

export default function Header() {
  const navigate = useNavigate();

  const logout = () => {
    setAuthHeader(null);
    setRoleHeader(null);
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
