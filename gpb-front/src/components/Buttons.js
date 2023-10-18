import * as React from 'react';
import { isUserAuth } from '../helpers/axios_helper';
import { useTranslation } from "react-i18next";

export default function Buttons(props) {
  const { t } = useTranslation();

  return (
    <div className="row">
      <div className="col-md-12 text-center">
        <button className={isUserAuth() ? "btn btn-dark" : "btn btn-primary"} style={{ margin: '15px' }} onClick={isUserAuth() ? props.logout : props.login}>
          {isUserAuth() ? t('app.logout')  : t('app.login')}
        </button>
      </div>
    </div>
  );
};