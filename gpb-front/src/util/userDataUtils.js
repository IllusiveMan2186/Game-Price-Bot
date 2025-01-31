export const getEmail = () => window.localStorage.getItem('EMAIL');

export const setEmail = (email) => {
    window.localStorage.setItem('EMAIL', email);
};

export const getLocale = () => window.localStorage.getItem('LOCALE');

export const setLocale = (locale) => {
    if (locale) {
        window.localStorage.setItem('LOCALE', locale);
    } else {
        window.localStorage.removeItem('LOCALE');
    }
    console.info(`Locale set to: ${locale}`);
};

export const getLinkToken = () => window.localStorage.getItem('LINK_TOKEN');

export const setLinkToken = (token) => {
    window.localStorage.setItem('LINK_TOKEN', token);
};

export const getUserRole = () => window.localStorage.getItem('ROLE');

export const setUserRole = (role) => {
    window.localStorage.setItem('ROLE', role);
};

export const isUserAdmin = () => getUserRole() === 'ROLE_ADMIN';