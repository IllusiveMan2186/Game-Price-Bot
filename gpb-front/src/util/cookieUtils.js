export const areCookiesEnabled = () => {
    document.cookie = "test_cookie=1; path=/";
    const cookiesEnabled = document.cookie.indexOf("test_cookie") !== -1;
    document.cookie = "test_cookie=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;";
    return cookiesEnabled;
};
