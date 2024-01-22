import * as React from 'react'
import classNames from 'classnames'
import Message from '../../util/message';
import { useNavigate } from 'react-router-dom'
import { loginRequest, registerRequest, resendActivationEmailRequest } from '../../request/userRequests';
import { validateUserFieldInput } from '../../util/validation';

export default function Login() {

    const navigate = useNavigate();

    const [active, setActive] = React.useState("login");
    const [errorMessage, setErrorMessage] = React.useState("");

    const [email, setEmail] = React.useState("");
    const [password, setPassword] = React.useState("");
    const [confirmPassword, setConfirmPassword] = React.useState("");

    const [errorEmail, setErrorEmail] = React.useState("");
    const [errorPassword, setErrorPassword] = React.useState("");
    const [errorConfirmPassword, setErrorConfirmPassword] = React.useState("");


    const onChangeHandler = (event) => {
        switch (event.target.name) {
            case "email":
                setEmail(event.target.value)
                break;
            case "password":
                setPassword(event.target.value)
                break;
            case "confirmPassword":
                setConfirmPassword(event.target.value)
                break;
            default:
                break;
        }
        validateInput(event);
    };

    const onSubmitLogin = (e) => {
        loginRequest(e, email, password, setErrorMessage, navigate)
    };

    const onSubmitResendEmail = e => {
        resendActivationEmailRequest(e, email, navigate)
    }

    const onSubmitRegistration = (e) => {
        if (isRegistrationFormValid()) {
            registerRequest(e, email, password, setErrorMessage, navigate);
        }

    }

    const isRegistrationFormValid = () => {
        return isAllFieldsFiled() && isNoErrors()
    }

    const cleanAll = (string) => {
        setActive(string)
        setEmail("")
        setPassword("")
        setConfirmPassword("")
        setErrorEmail("")
        setErrorPassword("")
        setErrorConfirmPassword("")
        setErrorMessage("");
    }

    const isLoginFormValid = () => {
        return !isEmptyString(email) && !isEmptyString(password)
    }


    const isNoErrors = () => {
        return isEmptyString(errorEmail)
            && isEmptyString(errorPassword)
            && isEmptyString(errorConfirmPassword);
    }

    const isAllFieldsFiled = () => {
        return !isEmptyString(email)
            && !isEmptyString(password)
            && !isEmptyString(confirmPassword);
    }

    const isEmptyString = (string) => {
        return string.length == 0;
    }

    const validateInput = e => {
        validateUserFieldInput(e, password, setErrorEmail, setErrorPassword, setErrorConfirmPassword)
    }

    return (
        <div className="row justify-content-center login-form">
            <div className="col-4 ">
                <ul className="nav nav-pills nav-justified mb-3" id="ex1" role="tablist">
                    <li className="nav-item" role="presentation">
                        <button className={classNames("nav-link", active === "login" ? "active" : "")} id="tab-login"
                            onClick={() => cleanAll("login")}>{<Message string={'app.login'} />}</button>
                    </li>
                    <li className="nav-item" role="presentation">
                        <button className={classNames("nav-link", active === "register" ? "active" : "")} id="tab-register"
                            onClick={() => cleanAll("register")}><Message string={'app.registr'} /></button>
                    </li>
                </ul>

                <div className="tab-content">
                    <div className={classNames("tab-pane", "fade", active === "login" ? "show active" : "")} id="pills-login" >
                        <form onSubmit={onSubmitLogin}>

                            <div className="form-outline mb-4">
                                <label className="form-label" htmlFor="loginName"><Message string={'app.login.form.email'} /></label>
                                <input type="email" id="1" name="email" className="form-control" onChange={onChangeHandler} />
                            </div>

                            <div className="form-outline mb-4">
                                <label className="form-label" htmlFor="loginPassword"><Message string={'app.login.form.password'} /></label>
                                <input type="password" id="2" name="password" className="form-control" onChange={onChangeHandler} />
                            </div>

                            <span className='Error'><Message string={errorMessage} /> </span>

                            {(errorMessage === "app.user.error.account.not.activated") && 
                            <span className='Resend-link nav mb-3' onClick={onSubmitResendEmail}><Message string={'app.user.error.account.not.activated.send'} /> </span>}

                            <button type="submit" className="btn btn-primary btn-block mb-4" disabled={!isLoginFormValid()}><Message string={'app.login.form.singup'} /></button>

                        </form>
                    </div>
                    <div className={classNames("tab-pane", "fade", active === "register" ? "show active" : "")} id="pills-register" >
                        <form onSubmit={onSubmitRegistration}>

                            <div className="form-outline mb-4">
                                <label className="form-label" htmlFor="email"><Message string={'app.login.form.email'} /></label>
                                <input type="email" id="1" name="email" className="form-control" onChange={onChangeHandler}
                                    onBlur={validateInput} />
                                <span className='Error'>{errorEmail}</span>
                            </div>

                            <div className="form-outline mb-4">
                                <label className="form-label" htmlFor="registerPassword"><Message string={'app.login.form.password'} /></label>
                                <input type="password" id="2" name="password" className="form-control" onChange={onChangeHandler}
                                    onBlur={validateInput} />
                                <span className='Error'>{errorPassword}</span>
                            </div>

                            <div className="form-outline mb-4">
                                <label className="form-label" htmlFor="registerPasswordConfirm"><Message string={'app.registr.form.pass.conf'} /></label>
                                <input type="password" id="3" name="confirmPassword" className="form-control"
                                    onChange={onChangeHandler} onBlur={validateInput} />
                                <span className='Error'>{errorConfirmPassword}</span>
                            </div>

                            <span className='Error'><Message string={errorMessage} /> </span>

                            <button type="submit" className="btn btn-primary btn-block mb-3" disabled={!isRegistrationFormValid()}><Message string={'app.registr.form.reg.buttom'} /></button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}