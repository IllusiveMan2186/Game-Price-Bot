import * as React from 'react'
import classNames from 'classnames'
import Message from '../../util/message';
import { passwordChangeRequest } from '../../request/userRequests';
import { validateUserFieldInput } from '../../util/validation';
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
        passwordChangeRequest(e, password, setErrorMessage, navigate)
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

    const validateInput = e => {
        validateUserFieldInput(e, password, null, setErrorPassword, setErrorConfirmPassword)
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