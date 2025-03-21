import React, { useState } from 'react';
import classNames from 'classnames';
import * as Yup from 'yup';

import { useNavigation } from "@contexts/NavigationContext";
import Message from '@util/message';
import { useLinkActions } from '@hooks/user/useLinkActions';

export default function LinkPage() {
    const navigate = useNavigation();
    const { accountLinkRequest } = useLinkActions();

    const [token, setToken] = useState('');
    const [errorToken, setErrorToken] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    // Yup schema for Ttokenoken validation
    const tokenValidationSchema = Yup.string()
        .required(<Message string="app.user.link.token.error.required" />);

    // Handler for token input change
    const onChangeHandler = (event) => {
        const { value } = event.target;
        setToken(value);
        validateToken(value);
    };

    // Validates the token using Yup
    const validateToken = (value) => {
        tokenValidationSchema
            .validate(value)
            .then(() => setErrorToken(''))
            .catch((error) => setErrorToken(error.message));
    };

    // Validates the form before submission
    const isFormValid = () => token.trim() !== '' && errorToken === '';

    // Submits the token change request
    const onSubmitTokenChange = (event) => {
        event.preventDefault();
        accountLinkRequest(token, setErrorMessage, navigate);
    };

    return (
        <div className="app-user-info">
            <span className="app-user-info-title">
                <Message string="app.user.link.enter" />
            </span>
            {errorMessage && (
                <span className="Error">
                    <Message string={errorMessage} />
                </span>
            )}
            <div className="tab-content">
                <div className={classNames('tab-pane', 'fade', 'show', 'active')} id="pills-register">
                    <form onSubmit={onSubmitTokenChange}>
                        <div className="form-outline mb-4">
                            <label className="form-label" htmlFor="token">
                                <Message string="app.user.link.enter.description" />
                            </label>
                            <input
                                type="token"
                                id="token"
                                name="token"
                                className="form-control"
                                value={token}
                                onChange={onChangeHandler}
                                onBlur={() => validateToken(token)}
                            />
                            {errorToken && <span className="Error">{errorToken}</span>}
                        </div>
                        <button
                            type="submit"
                            className="btn btn-primary btn-block mb-3"
                            disabled={!isFormValid()}
                        >
                            <Message string="app.user.link.form.button" />
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
}
