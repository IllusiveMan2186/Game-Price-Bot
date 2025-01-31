import React from 'react';
import ReactDOM from 'react-dom/client';

import 'bootstrap/dist/css/bootstrap.min.css';

import App from '@root/App';

import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import translationEN from '@locales/en/translation.json';
import translationRU from '@locales/ru/translation.json';
import translationUA from '@locales/ua/translation.json';


const locale = window.localStorage.getItem('LOCALE') || navigator.language;

i18n
    .use(initReactI18next)
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
        lng: locale,
        fallbackLng: "en",

        interpolation: {
            escapeValue: false
        }
    });

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <App />
);

