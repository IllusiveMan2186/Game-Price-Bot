import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import 'bootstrap/dist/css/bootstrap.min.css'
import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import translationEN from './locales/en/translation.json';
import translationRU from './locales/ru/translation.json';
import translationUA from './locales/ua/translation.json';

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

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
