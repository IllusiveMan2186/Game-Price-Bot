import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import classNames from 'classnames';
import * as Yup from 'yup';

import Message from '../../util/message';
import { emailChangeRequest } from '../../services/userRequests';

export default function EmailChange() {
    const navigate = useNavigate();

    const [email, setEmail] = useState('');
    const [errorEmail, setErrorEmail] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    // Yup schema for email validation
    const emailValidationSchema = Yup.string()
        .email(<Message string="app.login.form.error.wrong.email" />)
        .required(<Message string="app.login.form.error.empty.email" />);

    // Handler for email input change
    const onChangeHandler = (event) => {
        const { value } = event.target;
        setEmail(value);
        validateEmail(value);
    };

    // Validates the email using Yup
    const validateEmail = (value) => {
        emailValidationSchema
            .validate(value)
            .then(() => setErrorEmail(''))
            .catch((error) => setErrorEmail(error.message));
    };

    // Validates the form before submission
    const isFormValid = () => email.trim() !== '' && errorEmail === '';

    // Submits the email change request
    const onSubmitEmailChange = (event) => {
        event.preventDefault();
        emailChangeRequest(event, email, setErrorMessage, navigate);
    };

    return (
        <div className="App-user-info">
            <span className="App-user-info-title">
                <Message string="app.user.change.email" />
            </span>
            {errorMessage && (
                <span className="Error">
                    <Message string={errorMessage} />
                </span>
            )}
            <div className="tab-content">
                <div className={classNames('tab-pane', 'fade', 'show', 'active')} id="pills-register">
                    <form onSubmit={onSubmitEmailChange}>
                        <div className="form-outline mb-4">
                            <label className="form-label" htmlFor="email">
                                <Message string="app.login.form.email" />
                            </label>
                            <input
                                type="email"
                                id="email"
                                name="email"
                                className="form-control"
                                value={email}
                                onChange={onChangeHandler}
                                onBlur={() => validateEmail(email)}
                            />
                            {errorEmail && <span className="Error">{errorEmail}</span>}
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
