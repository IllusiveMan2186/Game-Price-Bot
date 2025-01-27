// --- AUTHENTICATION UTILITIES ---

/**
 * Get the authentication token from localStorage.
 * @returns {string|null} The stored auth token, or null if not present.
 */
export const getAuthToken = () => window.localStorage.getItem('auth_token');

/**
 * Set the authentication token in localStorage.
 * @param {string|null} token - The auth token to store, or null to remove it.
 */
export const setAuthToken = (token) => {
    if (token) {
        window.localStorage.setItem('auth_token', token);
    } else {
        window.localStorage.removeItem('auth_token');
    }
};

/**
 * Check if the user is authenticated.
 * @returns {boolean} True if authenticated, false otherwise.
 */
export function isUserAuth() {
    const authToken = getAuthToken();
    return authToken && authToken !== "null";
}

// --- USER ROLE UTILITIES ---

/**
 * Get the user's role from localStorage.
 * @returns {string|null} The stored role, or null if not present.
 */
export const getUserRole = () => window.localStorage.getItem('role');

/**
 * Set the user's role in localStorage.
 * @param {string|null} role - The role to store, or null to remove it.
 */
export const setUserRole = (role) => {
    if (role) {
        window.localStorage.setItem('role', role);
    } else {
        window.localStorage.removeItem('role');
    }
};

/**
 * Check if the user is an admin.
 * @returns {boolean} True if the user role is "ROLE_ADMIN", false otherwise.
 */
export const isUserAdmin = () => getUserRole() === 'ROLE_ADMIN';

// --- EMAIL UTILITIES ---

/**
 * Get the user's email from localStorage.
 * @returns {string|null} The stored email, or null if not present.
 */
export const getEmail = () => window.localStorage.getItem('email');

/**
 * Get the user's link token
 * @returns {string|null} The stored email, or null if not present.
 */
export const getLinkToken = () => window.localStorage.getItem('linkToken');

/**
 * Set the user's email in localStorage.
 * @param {string|null} email - The email to store, or null to remove it.
 */
export const setEmail = (email) => {
    if (email) {
        window.localStorage.setItem('email', email);
    } else {
        window.localStorage.removeItem('email');
    }
};

// --- LOCALE UTILITIES ---

/**
 * Get the user's locale from localStorage.
 * @returns {string|null} The stored locale, or null if not present.
 */
export const getLocale = () => window.localStorage.getItem('locale');

/**
 * Set the user's locale in localStorage.
 * @param {string|null} locale - The locale to store, or null to remove it.
 */
export const setLocale = (locale) => {
    if (locale) {
        window.localStorage.setItem('locale', locale);
    } else {
        window.localStorage.removeItem('locale');
    }
    console.info(`Locale set to: ${locale}`);
};

export const setAuthHeader = (token) => {
    window.localStorage.setItem('auth_token', token);
};

export const setLocaleHeader = (locale) => {
    window.localStorage.setItem('locale', locale);
};

export const setRoleHeader = (role) => {
    window.localStorage.setItem('role', role);
};

export const setEmailHeader = (email) => {
    window.localStorage.setItem('email', email);
};

export const setLinkToken = (token) => {
    window.localStorage.setItem('linkToken', token);
};

export const defaultRequestErrorCheck = (error) => {
    if (error?.response?.status === 401) {
        setAuthToken(null);
        setUserRole(null);
    }
};

// --- ERROR HANDLING ---

/**
 * Handle default request errors.
 * Clears auth and role if the error status is 401.
 * @param {object} error - The error object from Axios.
 */
export const handleRequestError = (error) => {
    console.error('Request Error:', error.response?.status, error.response?.data?.error);
    if (error.response?.status === 401) {
        setAuthToken(null);
        setUserRole(null);
    }
};
