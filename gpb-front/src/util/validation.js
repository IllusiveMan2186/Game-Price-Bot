import * as React from 'react'

import Message from './message';

export const validateUserFieldInput = (event, password, setErrorEmail, setErrorPassword, setErrorConfirmPassword) => {
    let name = event.target.name;
    let value = event.target.value;

    switch (name) {
        case "email":
            if (!value) {
                setErrorEmail(<Message string={'app.login.form.error.empty.email'} />)
            } else if (!isValidEmail(value)) {
                setErrorEmail(<Message string={'app.login.form.error.wrong.email'} />)
            } else {
                setErrorEmail("")
            }
            break;

        case "password":
            if (!value) {
                setErrorPassword(<Message string={'app.login.form.error.empty.password'} />)
            } else if (value !== password) {
                setErrorPassword(<Message string={'app.registr.form.error.not.match.pass.conf'} />)
            } else {
                setErrorPassword("")
            }
            break;

        case "confirmPassword":
            if (!value) {
                setErrorConfirmPassword(<Message string={'app.registr.form.error.empty.pass.conf'} />)
            } else if (password && value !== password) {
                setErrorConfirmPassword(<Message string={'app.registr.form.error.not.match.pass.conf'} />)
            } else {
                setErrorConfirmPassword("")
            }
            break;

        default:
            break;
    }
}

const isValidEmail = (email) => {
    return /\S+@\S+\.\S+/.test(email);
}