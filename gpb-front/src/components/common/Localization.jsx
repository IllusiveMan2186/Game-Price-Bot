import React, { useState } from 'react';
import { changeLanguage } from 'i18next';

import Message from '@util/message';
import { useAuth } from "@contexts/AuthContext";
import { getLocale, setLocale } from '@util/userDataUtils';
import { useUserActions } from '@hooks/user/useUserActions';

const Localization = () => {
    const [locale, setLocaleState] = useState(getLocale());
    const { isUserAuth } = useAuth();
    const { localeChangeRequest } = useUserActions();

    const onChangeHandler = (value) => {
        setLocaleState(value);
        changeLanguage(value);
        setLocale(value);

        if (isUserAuth) {
            localeChangeRequest(value);
        }
    };

    return (
        <div id="footer-language-choice" className="col-md-3 col-lg-2 col-xl-2 mx-auto mt-3">
            <h6 className="text-uppercase mb-4 font-weight-bold">
                <Message string={'app.footer.language'} />
            </h6>
            <button
                id='ru-locale'
                className="btn btn-primary btn-floating m-1"
                style={{ backgroundColor: locale === "ru" ? "#0082ca" : "#333333" }}
                onClick={() => onChangeHandler('ru')}
            >
                RU
            </button>
            <button
                id='ua-locale'
                className="btn btn-primary btn-floating m-1"
                style={{ backgroundColor: locale === "ua" ? "#0082ca" : "#333333" }}
                onClick={() => onChangeHandler('ua')}
            >
                UA
            </button>
            <button
                id='en-locale'
                className="btn btn-primary btn-floating m-1"
                style={{ backgroundColor: locale === "en" ? "#0082ca" : "#333333" }}
                onClick={() => onChangeHandler('en')}
            >
                EN
            </button>
        </div>
    );
};

export default Localization;
