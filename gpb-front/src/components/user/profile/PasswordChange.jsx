import React, { useState } from 'react';
import classNames from 'classnames';
import * as Yup from 'yup';


import { useAuthActions } from '@hooks/user/useAuthActions';
import { useAuth } from "@contexts/AuthContext";

import { useNavigation } from "@contexts/NavigationContext";
import { useUserActions } from '@hooks/user/useUserActions';

import Message from '@util/message';

export default function PasswordChange() {
    const navigate = useNavigation();
    const { passwordChangeRequest } = useUserActions();

    const { userLogoutRequest } = useAuthActions()
    const { logout } = useAuth();

    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [oldErrorPassword, setOLdErrorPassword] = useState('');
    const [newErrorPassword, setNewErrorPassword] = useState('');
    const [errorConfirmPassword, setErrorConfirmPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    // Yup validation schema
    const validationSchema = Yup.object().shape({
        oldPassword: Yup.string()
            .required(<Message string={'app.login.form.error.empty.password'} />),

        newPassword: Yup.string()
            .min(8, <Message string={'app.login.form.error.short.password'} />)
            .max(64, <Message string={'app.login.form.error.long.password'} />)
            .matches(/[A-Z]/, <Message string={'app.registr.form.error.uppercase.password'} />)
            .matches(/[a-z]/, <Message string={'app.registr.form.error.lowercase.password'} />)
            .matches(/[0-9]/, <Message string={'app.registr.form.error.number.password'} />)
            .matches(/[@$!%*?&]/, <Message string={'app.registr.form.error.specialchar.password'} />)
            .matches(/^[^<>]*$/, <Message string={'app.registr.form.error.xss.password'} />)
            .required(<Message string={'app.login.form.error.empty.password'} />),

        confirmPassword: Yup.string()
            .oneOf([Yup.ref('newPassword'), null], <Message string="app.registr.form.error.not.match.pass.conf" />)
            .required(<Message string="app.login.form.error.empty.password" />),
    });

    // Handles input changes and validates on the fly
    const onChangeHandler = (event) => {
        const { name, value } = event.target;

        if (name === 'oldPassword') {
            setOldPassword(value);
            validateField('oldPassword', value);
        } else if (name === 'newPassword') {
            setNewPassword(value);
            validateField('newPassword', value);
        } else if (name === 'confirmPassword') {
            setConfirmPassword(value);
            validateField('confirmPassword', value);
        }
    };

    // Validates a single field using Yup
    const validateField = (fieldName, value) => {
        validationSchema
            .validateAt(fieldName, { newPassword, confirmPassword: fieldName === 'confirmPassword' ? value : confirmPassword })
            .then(() => {
                if (fieldName === 'oldPassword') setOLdErrorPassword('');
                if (fieldName === 'newPassword') setNewErrorPassword('');
                if (fieldName === 'confirmPassword') setErrorConfirmPassword('');
            })
            .catch((error) => {
                if (fieldName === 'oldPassword') setOLdErrorPassword(error.message);
                if (fieldName === 'newPassword') setNewErrorPassword(error.message);
                if (fieldName === 'confirmPassword') setErrorConfirmPassword(error.message);
            });
    };

    // Checks if the form is valid
    const isFormValid = () => {

        return !oldErrorPassword && !newErrorPassword && !errorConfirmPassword
            && oldPassword.trim() !== '' && newPassword.trim() !== '' && confirmPassword.trim() !== '';
    };


    const logoutCall = async () => {
        console.info("logout");
        logout();

        await userLogoutRequest();
        navigate("/")
    };

    // Handles form submission
    const onSubmitPasswordChange = (event) => {
        event.preventDefault();
        validationSchema
            .validate({ oldPassword, newPassword, confirmPassword })
            .then(() => {
                passwordChangeRequest(oldPassword, newPassword, setErrorMessage, logoutCall);
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
                            <label className="form-label" htmlFor="registerOldPassword">
                                <Message string="app.old.password" />
                            </label>
                            <input
                                type="password"
                                id="registerOldPassword"
                                name="oldPassword"
                                className="form-control"
                                value={oldPassword}
                                onChange={onChangeHandler}
                                onBlur={() => validateField('oldPassword', oldPassword)}
                            />
                            {newErrorPassword && <span className="Error">{newErrorPassword}</span>}
                        </div>

                        <div className="form-outline mb-4">
                            <label className="form-label" htmlFor="registerNewPassword">
                                <Message string="app.new.password" />
                            </label>
                            <input
                                type="password"
                                id="registerNewPassword"
                                name="newPassword"
                                className="form-control"
                                value={newPassword}
                                onChange={onChangeHandler}
                                onBlur={() => validateField('newPassword', newPassword)}
                            />
                            {newErrorPassword && <span className="Error">{newErrorPassword}</span>}
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
