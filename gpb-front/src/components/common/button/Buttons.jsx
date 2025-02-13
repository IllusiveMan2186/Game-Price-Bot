import { useNavigation } from "@contexts/NavigationContext";
import { useAuth } from "@contexts/AuthContext";
import Message from '@util/message';

import './Button.css';

export default function Buttons(props) {
  const navigate = useNavigation();

  const { isUserAuth } = useAuth();

  const handleClickLogin = () => {
    navigate("/login");
  };

  if (isUserAuth() === null) {
    return <div>Loading...</div>; // Show loading state while checking authentication
  }

  return (
    <div className="row">
      <div className="col-md-12 text-center">
        {isUserAuth() ? (
          <DropDown logout={props.logout} />
        ) : (
          <LoginButton handleClick={handleClickLogin} />
        )}
      </div>
    </div>
  );
};

function LoginButton(props) {

  return (
    <div style={{ margin: '15px' }}>
      <button id="login-button" className={"btn btn-primary"} onClick={props.handleClick}>
        <Message string={'app.login'} />
      </button>
    </div>
  )
}

function DropDown(props) {
  const navigate = useNavigation();

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

  const handleGetAccountLinkTokenClick = () => {
    navigate("/link/token");
  }

  return (
    <div className="dropdown">
      <button id="profile-dropdown-button" className="btn btn-primary"><Message string={'app.profile'} /></button>
      <div className="dropdown-content">
        <a id="change-email-button" onClick={handleChangeEmailClick}><Message string={'app.user.change.email'} /></a>
        <a id="change-password-button" onClick={handleChangePasswordClick}><Message string={'app.user.change.password'} /></a>
        <a id="user-gameList-button" onClick={handleGameListClick}><Message string={'app.profile.game.list'} /></a>
        <a id="user-gameList-button" onClick={handleAccountLinkClick}><Message string={'app.profile.account.link'} /></a>
        <a id="user-gameList-button" onClick={handleGetAccountLinkTokenClick}><Message string={'app.profile.account.link.token'} /></a>
        <a id="logout-button" onClick={props.logout}><Message string={'app.profile.logout'} /></a>
      </div>
    </div>
  )
}