import { useState, useEffect } from "react";
import { getAuthToken } from "./authUtils";
import { checkAuthRequest } from "@services/userRequests";

export function useIsUserAuth() {
    const [isAuthByCookie, setIsAuthByCookie] = useState(null);

    useEffect(() => {
        const checkAuth = async () => {
            const cachedAuth = window.localStorage.getItem("IS_AUTHENTICATED");
            const authToken = getAuthToken();

            if (authToken && authToken !== "null") {
                return setIsAuthByCookie(true);
            }

            if (cachedAuth === "true") {
                return setIsAuthByCookie(true);
            }

            try {
                const isAuthenticated = await checkAuthRequest();
                console.log("User Authenticated:", isAuthenticated);
                setIsAuthByCookie(isAuthenticated);
            } catch (error) {
                console.error("Auth check failed:", error);
                window.localStorage.removeItem("IS_AUTHENTICATED");
                setIsAuthByCookie(false);
            }
        };

        checkAuth();
    }, []);

    return isAuthByCookie;
}
