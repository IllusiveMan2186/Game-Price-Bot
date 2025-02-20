export const getLocale = () => window.localStorage.getItem('LOCALE');

export const setLocale = (locale) => {
    if (locale) {
        window.localStorage.setItem('LOCALE', locale);
    } else {
        window.localStorage.removeItem('LOCALE');
    }
    console.info(`Locale set to: ${locale}`);
};