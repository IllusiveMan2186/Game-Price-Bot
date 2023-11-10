import * as React from 'react'
import classNames from 'classnames'
import Message from './Message';
import { request } from '../helpers/axios_helper';
import { setEmailHeader } from '../helpers/axios_helper';
import { useNavigate } from 'react-router-dom'

export default function PasswordChange(props) {

    const navigate = useNavigate();

    const [errorMessage, setErrorMessage] = React.useState("");

    const [password, setPassword] = React.useState("");
    const [confirmPassword, setConfirmPassword] = React.useState("");

    const [errorPassword, setErrorPassword] = React.useState("");
    const [errorConfirmPassword, setErrorConfirmPassword] = React.useState("");

    const onChangeHandler = (event) => {
        switch (event.target.name) {
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

    const onSubmitPasswordChange = (e) => {
        onPasswordChange(e, password)
    };

    const isFormValid = () => {
        return isAllFieldsFiled() && isNoErrors()
    }

    const isNoErrors = () => {
        return isEmptyString(errorPassword)
            && isEmptyString(errorConfirmPassword);
    }

    const isAllFieldsFiled = () => {
        return !isEmptyString(password)
            && !isEmptyString(confirmPassword);
    }

    const isEmptyString = (string) => {
        return string.length == 0;
    }

    const onPasswordChange = (event, password) => {
        event.preventDefault();
        request(
            "PUT",
            "/user/password",
            {
                password: password
            }).then(
                (response) => {
                    navigate("/")
                }).catch(
                    (error) => {
                        setErrorMessage(error.response.data)
                    }
                );
    };

    const validateInput = e => {
        let name = e.target.name;
        let value = e.target.value;

        switch (name) {
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

    return (
        <div class='App-user-info '>
            <span class="App-user-info-title" ><Message string={'app.user.change.password'} /></span>
            <span className='Error'><Message string={errorMessage} /> </span>
            <div className="tab-content">
                <div className={classNames("tab-pane", "fade", "show active")} id="pills-register" >
                    <form onSubmit={onSubmitPasswordChange}>

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

                        <button type="submit" className="btn btn-primary btn-block mb-3" disabled={!isFormValid()}><Message string={'app.registr.form.reg.buttom'} /></button>
                    </form>
                </div>
            </div>
        </div >
    );

};