import * as React from 'react';
import { isUserAuth } from '../helpers/axios_helper';

export default function Buttons(props) {
  return (
    <div className="row">
      <div className="col-md-12 text-center">
        <button className={isUserAuth() ? "btn btn-dark" : "btn btn-primary"} style={{ margin: '15px' }} onClick={isUserAuth() ? props.logout : props.login}>
          {isUserAuth() ? "Logout" : "Login"}
        </button>
      </div>
    </div>
  );
};