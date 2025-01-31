export const getAuthToken = () => window.localStorage.getItem('AUTH_TOKEN');

export const setAuthToken = (token) => {
    window.localStorage.removeItem('LINK_TOKEN');
    window.localStorage.setItem('AUTH_TOKEN', token);
};

export const setAuthFlag = () => {
    window.localStorage.setItem("IS_AUTHENTICATED", "true");
};

export const logout = () => {
    window.localStorage.removeItem('AUTH_TOKEN');
    window.localStorage.removeItem('ROLE');
    window.localStorage.removeItem('EMAIL');
    window.localStorage.removeItem("IS_AUTHENTICATED");
};
