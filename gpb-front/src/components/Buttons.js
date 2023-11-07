import * as React from 'react';
import { isUserAuth } from '../helpers/axios_helper';
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";

export default function Buttons(props) {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const handleClick = () => {
    navigate("/login");
}

  return (
    <div className="row">
      <div className="col-md-12 text-center">
        <button className={isUserAuth() ? "btn btn-dark" : "btn btn-primary"} 
        style={{ margin: '15px' }} onClick={isUserAuth() ? props.logout : handleClick}>
          {isUserAuth() ? t('app.logout')  : t('app.login')}
        </button>
      </div>
    </div>
  );
};