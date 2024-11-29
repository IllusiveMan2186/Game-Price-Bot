import * as React from 'react';
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';
import { useNavigate } from 'react-router-dom';

import Message from '../../../util/message';
import { loginRequest } from '../../../services/auth';
import { resendActivationEmailRequest } from '../../../services/userRequests';

export default function LoginForm() {

    const navigate = useNavigate();

    const [errorMessage, setErrorMessage] = React.useState("");
    const [confirmedEmail, setConfirmedEmail] = React.useState("");

    const LoginSchema = Yup.object().shape({
        email: Yup.string().required(<Message string={'app.login.form.error.empty.email'} />),
        password: Yup.string().required(<Message string={'app.login.form.error.empty.password'} />),
    });

    const onSubmitResendEmail = () => {
        resendActivationEmailRequest(confirmedEmail, navigate)
    }

    const handleSubmit = (values, { }) => {
        setConfirmedEmail(values.email);
        loginRequest(values.email, values.password, setErrorMessage, navigate);
    };


    return (
        <Formik
            initialValues={{ email: '', password: '' }}
            validationSchema={LoginSchema}
            validateOnMount
            onSubmit={handleSubmit}
        >
            {({ isValid }) => (
                <Form>
                    <div className="form-outline mb-4 Column ">
                        <label className="form-label" htmlFor="email"><Message string={'app.login.form.email'} /></label>
                        <Field type="email" name="email" className="form-control" />
                        <ErrorMessage name="email" component="div" className="Error" />
                    </div>

                    <div className="form-outline mb-4 Column">
                        <label className="form-label" htmlFor="password"><Message string={'app.login.form.password'} /></label>
                        <Field type="password" name="password" className="form-control" />
                        <ErrorMessage name="password" component="div" className="Error" />
                    </div>

                    <span className='Error form-outline mb-4 Column '><Message string={errorMessage} /></span>

                    {(errorMessage === "app.user.error.account.not.activated") &&
                        <span className='Resend-link nav mb-3' onClick={onSubmitResendEmail}>
                            <Message string={'app.user.error.account.not.activated.send'} />
                        </span>}

                    <button type="submit" className="btn btn-primary btn-block mb-4" disabled={!isValid}>
                        {<Message string={'app.login.form.singup'} />}
                    </button>
                </Form>
            )}
        </Formik>
    );
}