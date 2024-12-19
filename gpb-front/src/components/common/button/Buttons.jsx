import * as React from 'react';
import { useNavigate } from 'react-router-dom';

import { isUserAuth } from '@util/authService';
import Message from '@util/message';

import './Button.css';

export default function Buttons(props) {
  const navigate = useNavigate();

  const handleClickLogin = () => {
    navigate("/login");
  }

  return (
    <div className="row">
      <div className="col-md-12 text-center">
        {isUserAuth() ? <DropDown logout={props.logout} /> : <LoginButton handleClick={handleClickLogin} />}
      </div>
    </div>
  );
};

function LoginButton(props) {

  return (
    <div style={{ margin: '15px' }}>
      <button id="login-button"  className={"btn btn-primary"} onClick={props.handleClick}>
        <Message string={'app.login'} />
      </button>
    </div>
  )
}

function DropDown(props) {
  const navigate = useNavigate();

  const handleGameListClick = () => {
    navigate("/user/games/");
    navigate(0)
  }

  const handleChangeEmailClick = () => {
    navigate("/change/email");
  }

  const handleChangePasswordClick = () => {
    navigate("/change/password");
  }

  const handleAccountLinkClick = () => {
    navigate("/link");
  }

  return (
    <div class="dropdown">
      <button id="profile-dropdown-button" class="btn btn-primary"><Message string={'app.profile'} /></button>
      <div class="dropdown-content">
        <a id="change-email-button" onClick={handleChangeEmailClick}><Message string={'app.user.change.email'} /></a>
        <a id="change-password-button" onClick={handleChangePasswordClick}><Message string={'app.user.change.password'} /></a>
        <a id="user-gameList-button" onClick={handleGameListClick}><Message string={'app.profile.game.list'} /></a>
        <a id="user-gameList-button" onClick={handleAccountLinkClick}><Message string={'app.profile.account.link'} /></a>
        <a id="logout-button" onClick={props.logout}><Message string={'app.profile.logout'} /></a>
      </div>
    </div>
  )
}