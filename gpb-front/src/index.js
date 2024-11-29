import React from 'react';
import ReactDOM from 'react-dom/client';

import 'bootstrap/dist/css/bootstrap.min.css';

import App from '@root/App';

import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import translationEN from '@locales/en/translation.json';
import translationRU from '@locales/ru/translation.json';
import translationUA from '@locales/ua/translation.json';


const locale = window.localStorage.getItem('locale') || navigator.language;

i18n
    .use(initReactI18next) // passes i18n down to react-i18next
    .init({
        resources: {
            en: {
                translation: translationEN
            },
            ru: {
                translation: translationRU
            },
            ua: {
                translation: translationUA
            }
        },
        lng: locale, // if you're using a language detector, do not define the lng option
        fallbackLng: "en",

        interpolation: {
            escapeValue: false // react already safes from xss => https://www.i18next.com/translation-function/interpolation#unescape
        }
    });

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <App />
);

