import React, { useState } from 'react';
import classNames from 'classnames';
import * as Yup from 'yup';

import { useNavigation } from "@contexts/NavigationContext";
import Message from '@util/message';
import { useUserActions } from '@hooks/user/useUserActions';

export default function PasswordChange() {
    const navigate = useNavigation();
    const { passwordChangeRequest } = useUserActions();

    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [errorPassword, setErrorPassword] = useState('');
    const [errorConfirmPassword, setErrorConfirmPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    // Yup validation schema
    const validationSchema = Yup.object().shape({
        password: Yup.string()
            .required(<Message string="app.login.form.error.empty.password" />),
        confirmPassword: Yup.string()
            .oneOf([Yup.ref('password'), null], <Message string="app.registr.form.error.not.match.pass.conf" />)
            .required(<Message string="app.login.form.error.empty.password" />),
    });

    // Handles input changes and validates on the fly
    const onChangeHandler = (event) => {
        const { name, value } = event.target;

        if (name === 'password') {
            setPassword(value);
            validateField('password', value);
        } else if (name === 'confirmPassword') {
            setConfirmPassword(value);
            validateField('confirmPassword', value);
        }
    };

    // Validates a single field using Yup
    const validateField = (fieldName, value) => {
        validationSchema
            .validateAt(fieldName, { password, confirmPassword: fieldName === 'confirmPassword' ? value : confirmPassword })
            .then(() => {
                if (fieldName === 'password') setErrorPassword('');
                if (fieldName === 'confirmPassword') setErrorConfirmPassword('');
            })
            .catch((error) => {
                if (fieldName === 'password') setErrorPassword(error.message);
                if (fieldName === 'confirmPassword') setErrorConfirmPassword(error.message);
            });
    };

    // Checks if the form is valid
    const isFormValid = () => {
        return !errorPassword && !errorConfirmPassword && password.trim() !== '' && confirmPassword.trim() !== '';
    };

    // Handles form submission
    const onSubmitPasswordChange = (event) => {
        event.preventDefault();
        validationSchema
            .validate({ password, confirmPassword })
            .then(() => {
                passwordChangeRequest(event, password, setErrorMessage, navigate);
            })
            .catch((error) => {
                setErrorMessage(error.message);
            });
    };

    return (
        <div className="app-user-info">
            <span className="app-user-info-title">
                <Message string="app.user.change.password" />
            </span>
            {errorMessage && (
                <span className="Error">
                    <Message string={errorMessage} />
                </span>
            )}
            <div className="tab-content">
                <div className={classNames('tab-pane', 'fade', 'show', 'active')} id="pills-register">
                    <form onSubmit={onSubmitPasswordChange}>
                        <div className="form-outline mb-4">
                            <label className="form-label" htmlFor="registerPassword">
                                <Message string="app.login.form.password" />
                            </label>
                            <input
                                type="password"
                                id="registerPassword"
                                name="password"
                                className="form-control"
                                value={password}
                                onChange={onChangeHandler}
                                onBlur={() => validateField('password', password)}
                            />
                            {errorPassword && <span className="Error">{errorPassword}</span>}
                        </div>

                        <div className="form-outline mb-4">
                            <label className="form-label" htmlFor="registerPasswordConfirm">
                                <Message string="app.registr.form.pass.conf" />
                            </label>
                            <input
                                type="password"
                                id="registerPasswordConfirm"
                                name="confirmPassword"
                                className="form-control"
                                value={confirmPassword}
                                onChange={onChangeHandler}
                                onBlur={() => validateField('confirmPassword', confirmPassword)}
                            />
                            {errorConfirmPassword && <span className="Error">{errorConfirmPassword}</span>}
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary btn-block mb-3"
                            disabled={!isFormValid()}
                        >
                            <Message string="app.registr.form.reg.buttom" />
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
}
