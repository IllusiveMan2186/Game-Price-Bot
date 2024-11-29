import * as React from 'react';
import classNames from 'classnames';

import Message from '@util/message';
import LoginForm from '@components/auth/login/LoginForm';
import RegistrationForm from '@components/auth/registration/RegistrationForm';

import './AuthViewSwitcher.css';

export default function AuthViewSwitcher() {
    const LOGIN = "login";
    const REGISTER = "register";

    const [activeForm, setActiveForm] = React.useState(LOGIN);

    const changeActiveForm = (form) => {
        setActiveForm(form);
    };

    return (
        <div className="row justify-content-center login-form">
            <div className="col-4">
                <ul className="nav nav-pills nav-justified mb-3" id="ex1" role="tablist">
                    <li className="nav-item" role="presentation">
                        <button
                            className={classNames("nav-link", { active: activeForm === LOGIN })}
                            id="tab-login"
                            onClick={() => changeActiveForm(LOGIN)}
                        >
                            <Message string={'app.login'} />
                        </button>
                    </li>
                    <li className="nav-item" role="presentation">
                        <button
                            className={classNames("nav-link", { active: activeForm === REGISTER })}
                            id="tab-register"
                            onClick={() => changeActiveForm(REGISTER)}
                        >
                            <Message string={'app.registr'} />
                        </button>
                    </li>
                </ul>

                <div className="tab-content Column">
                    <div
                        className={classNames("tab-pane", "fade", {
                            "show active": activeForm === LOGIN,
                        })}
                        id="pills-login"
                    >
                        <LoginForm />
                    </div>
                    <div
                        className={classNames("tab-pane", "fade", {
                            "show active": activeForm === REGISTER,
                        })}
                        id="pills-register"
                    >
                        <RegistrationForm />
                    </div>
                </div>
            </div>
        </div>
    );
}