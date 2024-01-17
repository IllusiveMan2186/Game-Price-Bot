import * as React from 'react';
import { changeLanguage } from 'i18next';
import Message from '../../util/message';
import { localeChangeRequest } from '../../request/userRequests';
import { isUserAuth, getLocale } from '../../util/axios_helper'

export default class Localization extends React.Component {

    constructor() {
        super(null);
        this.state = {
            locale: getLocale()
        };
    };

    onChangeHandler = (value) => {
        this.setState({ ['locale']: value });
        changeLanguage(value);
        if (isUserAuth()) {
            localeChangeRequest(value)
        }
    };

    render() {
        return (

            <div className="col-md-3 col-lg-2 col-xl-2 mx-auto mt-3">
                <h6 className="text-uppercase mb-4 font-weight-bold">
                    <Message string={'app.footer.language'} />
                </h6>
                {/* Google */}
                <button
                    className="btn btn-primary btn-floating m-1"
                    style={{ backgroundColor: this.state.locale === "ru" ? "#0082ca" : "#333333" }}
                    onClick={(() => this.onChangeHandler('ru'))}
                >
                    RU
                </button >
                {/* Linkedin */}
                <button
                    className="btn btn-primary btn-floating m-1"
                    style={{ backgroundColor: this.state.locale === "ua" ? "#0082ca" : "#333333" }}
                    onClick={(() => this.onChangeHandler('ua'))}
                >
                    UA
                </button >
                {/* Github */}
                <button
                    className="btn btn-primary btn-floating m-1"
                    style={{ backgroundColor: this.state.locale === "en" ? "#0082ca" : "#333333" }}
                    onClick={(() => this.onChangeHandler('en'))}
                >
                    EN
                </button >
            </div>
        );
    }
}