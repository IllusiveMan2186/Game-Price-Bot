import * as React from 'react';
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';

import { useNavigation } from "@contexts/NavigationContext";
import Message from '@util/message';
import { useAuthActions } from '@hooks/user/useAuthActions';

export default function RegistrationForm() {

    const navigate = useNavigation();
    const { registerRequest } = useAuthActions()
    const [errorMessage, setErrorMessage] = React.useState("");

    const RegistrationSchema = Yup.object().shape({
        email: Yup.string().email(<Message string={'app.login.form.error.wrong.email'} />)
            .required(<Message string={'app.login.form.error.empty.email'} />),

        password: Yup.string()
            .min(8, <Message string={'app.login.form.error.short.password'} />)
            .max(64, <Message string={'app.login.form.error.long.password'} />)
            .matches(/[A-Z]/, <Message string={'app.registr.form.error.uppercase.password'} />)
            .matches(/[a-z]/, <Message string={'app.registr.form.error.lowercase.password'} />)
            .matches(/[0-9]/, <Message string={'app.registr.form.error.number.password'} />)
            .matches(/[@$!%*?&]/, <Message string={'app.registr.form.error.specialchar.password'} />)
            .matches(/^[^<>]*$/, <Message string={'app.registr.form.error.xss.password'} />)
            .required(<Message string={'app.login.form.error.empty.password'} />),

        confirmPassword: Yup.string()
            .oneOf([Yup.ref('password'), null], <Message string={'app.registr.form.error.not.match.pass.conf'} />)
            .required(<Message string={'app.registr.form.error.empty.pass.conf'} />),
    });

    const handleSubmit = (values, { }) => {
        registerRequest(values.email, values.password, setErrorMessage, navigate);
    };

    return (
        <Formik
            initialValues={{ email: '', password: '', confirmPassword: '' }}
            validationSchema={RegistrationSchema}
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

                    <div className="form-outline mb-4 Column">
                        <label className="form-label" htmlFor="password"><Message string={'app.registr.form.pass.conf'} /></label>
                        <Field type="password" name="confirmPassword" className="form-control" />
                        <ErrorMessage name="confirmPassword" component="div" className="Error" />
                    </div>

                    <span className='Error form-outline mb-4 Column '><Message string={errorMessage} /> </span>

                    <button type="submit" className="btn btn-primary btn-block mb-4" disabled={!isValid}>
                        {<Message string={'app.registr.form.reg.buttom'} />}
                    </button>
                </Form>
            )}
        </Formik>
    );
}
