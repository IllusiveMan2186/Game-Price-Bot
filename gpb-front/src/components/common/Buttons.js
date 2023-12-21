import * as React from 'react';
import { isUserAuth } from '../../util/axios_helper';
import { useNavigate } from "react-router-dom";
import Message from '../../util/message';

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
    <button className={"btn btn-primary"} style={{ margin: '15px' }} onClick={props.handleClick}>
      <Message string={'app.login'} />
    </button>
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

  return (
    <div class="dropdown">
      <button class="btn btn-primary"><Message string={'app.profile'} /></button>
      <div class="dropdown-content">
        <a onClick={handleChangeEmailClick}><Message string={'app.user.change.email'} /></a>
        <a onClick={handleChangePasswordClick}><Message string={'app.user.change.password'} /></a>
        <a onClick={handleGameListClick}><Message string={'app.profile.game.list'} /></a>
        <a onClick={props.logout}><Message string={'app.profile.logout'} /></a>
      </div>
    </div>
  )
}