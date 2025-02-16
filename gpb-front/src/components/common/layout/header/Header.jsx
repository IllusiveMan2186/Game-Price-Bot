import * as React from 'react';
import { useNavigation } from "@contexts/NavigationContext";

import Buttons from '@components/common/button/Buttons';
import { useAuth } from "@contexts/AuthContext";
import { useAuthActions } from '@hooks/user/useAuthActions';

import logo from '@assets/images/logo.png';
import './Header.css';

export default function Header() {
  const navigate = useNavigation();
  const { userLogoutRequest } = useAuthActions()
  const { logout } = useAuth();

  const handleLogout = async () => {
    logout();

    await userLogoutRequest();

    navigate(0);
  }

  const defaultPage = () => {
    navigate("/")
    navigate(0)
  };

  return (
    <header className="app-header">
      <div className="app-title" onClick={defaultPage}>
        <img src={logo} className="app-logo" alt="logo" />
        <h1 style={{ fontSize: 'auto' }}>GPB</h1>
      </div>

      <Buttons logout={handleLogout} />
    </header>
  );
};