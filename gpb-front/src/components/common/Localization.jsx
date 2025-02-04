import React, { useState } from 'react';
import { changeLanguage } from 'i18next';

import Message from '@util/message';
import { useIsUserAuth } from '@util/authHook';
import { getLocale, setLocale } from '@util/userDataUtils';
import { localeChangeRequest } from '@services/userRequests';

const Localization = () => {
    const [locale, setLocaleState] = useState(getLocale());
    const isUserAuth = useIsUserAuth();

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
                className="btn btn-primary btn-floating m-1"
                style={{ backgroundColor: locale === "ru" ? "#0082ca" : "#333333" }}
                onClick={() => onChangeHandler('ru')}
            >
                RU
            </button>
            <button
                className="btn btn-primary btn-floating m-1"
                style={{ backgroundColor: locale === "ua" ? "#0082ca" : "#333333" }}
                onClick={() => onChangeHandler('ua')}
            >
                UA
            </button>
            <button
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
