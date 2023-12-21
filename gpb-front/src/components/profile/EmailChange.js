import * as React from 'react'
import classNames from 'classnames'
import Message from '../../util/message';
import { emailChangeRequest } from '../../request/userRequests';
import { useNavigate } from 'react-router-dom'

export default function EmailChange(props) {

    const navigate = useNavigate();

    const [errorMessage, setErrorMessage] = React.useState("");

    const [email, setEmail] = React.useState("");

    const [errorEmail, setErrorEmail] = React.useState("");


    const onChangeHandler = (event) => {
        setEmail(event.target.value)

        validateInput(event);
    };

    const isFormValid = () => {
        return !isEmptyString(email) && isEmptyString(errorEmail)
    }

    const isEmptyString = (string) => {
        return string.length == 0;
    }

    const isValidEmail = (email) => {
        return /\S+@\S+\.\S+/.test(email);
    }

    const onSubmitEmailChange = (e) => {
        emailChangeRequest(e, email, setErrorMessage, navigate)
    };

    const validateInput = e => {
        let value = e.target.value;

        if (!value) {
            setErrorEmail(<Message string={'app.login.form.error.empty.email'} />)
        } else if (!isValidEmail(value)) {
            setErrorEmail(<Message string={'app.login.form.error.wrong.email'} />)
        } else {
            setErrorEmail("")
        }
    }

    return (
        <div class='App-user-info '>
            <span class='App-user-info-title'><Message string={'app.user.change.email'} /> </span>
            <span className='Error'><Message string={errorMessage} /> </span>
            <div className="tab-content">
                <div className={classNames("tab-pane", "fade", "show active")} id="pills-register" >
                    <form onSubmit={onSubmitEmailChange}>

                        <div className="form-outline mb-4">
                            <label className="form-label" htmlFor="email"><Message string={'app.login.form.email'} /></label>
                            <input type="email" id="1" name="email" className="form-control" onChange={onChangeHandler}
                                onBlur={validateInput} />
                            <span className='Error'>{errorEmail}</span>
                        </div>

                        <button type="submit" className="btn btn-primary btn-block mb-3" disabled={!isFormValid()}><Message string={'app.registr.form.reg.buttom'} /></button>
                    </form>
                </div>
            </div>
        </div>
    );

};