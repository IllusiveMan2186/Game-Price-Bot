/* httpService.js
 *
 * Axios HTTP service with:
 * - Access token injection
 * - Proactive token refresh based on JWT expiration
 * - Reactive refresh on 401 responses
 * - Request queuing to avoid duplicate refreshes
 */

import axios from "axios";
import { jwtDecode } from "jwt-decode";
import config from "@root/config";

// Axios instance for authorized API requests
const apiClient = axios.create({
  baseURL: config.BACKEND_SERVICE_URL,
  withCredentials: true,
  headers: { "Content-Type": "application/json" },
});

// Separate client for unauthenticated routes (e.g., refresh/logout)
const noAuthClient = axios.create({
  baseURL: config.BACKEND_SERVICE_URL,
  withCredentials: true,
  headers: { "Content-Type": "application/json" },
});

// External handlers registered from AuthContext
let getAccessTokenHandler = () => null;
let setAccessTokenHandler = () => { };
let getLinkTokenHandler = () => null;
let logoutHandler = () => { };
let navigateHandler = () => { };

// Called once to register handlers from the app
export function registerAuthHandlers({ getAccessToken, setAccessToken, getLinkToken, logout, navigate }) {
  getAccessTokenHandler = getAccessToken;
  setAccessTokenHandler = setAccessToken;
  getLinkTokenHandler = getLinkToken;
  logoutHandler = logout;
  navigateHandler = navigate;
}

// Queued requests waiting for a fresh token
let subscribers = [];

/**
 * Subscribes a request to be resumed after token is refreshed.
 * Only used in proactive refresh logic.
 */
function subscribe(cb) {
  // Useful for debugging refresh queuing
  console.info("📝 Subscribed request during token refresh");
  subscribers.push(cb);
}

/**
 * Notifies all queued requests with the new token result.
 */
function notifySubscribers(token) {
  console.info("📣 Notifying all subscribers");
  subscribers.forEach(cb => cb(token));
  subscribers = [];
}

/**
 * Attempts to refresh access token via backend.
 */
export async function refreshTokenRequest() {
  console.info("🔄 Performing token refresh…");
  try {
    const { data: newToken } = await noAuthClient.post("/refresh-token");

    if (!newToken || typeof newToken !== "string") {
      throw new Error("Invalid token");
    }

    setAccessTokenHandler(newToken);
    apiClient.defaults.headers.common.Authorization = `Bearer ${newToken}`;

    console.info("✅ Token successfully refreshed");
    return true;
  } catch (error) {
    console.error("❌ Refresh token failed:", error);

    // If refresh was unauthorized, log the user out
    if (error.response?.status === 401) {
      await logoutRequest();
      navigateHandler();
    }

    return false;
  }
}

/**
 * Sends logout request and triggers app logout behavior.
 */
export async function logoutRequest() {
  try {
    await noAuthClient.post("/logout-user");
  } catch (e) {
    console.error("Logout error:", e);
  }

  logoutHandler(navigateHandler);
}

/**
 * Logs all outgoing requests for debugging purposes.
 * You can remove this or wrap in a dev-only check if needed.
 */
apiClient.interceptors.request.use(
  config => {
    console.log("➡️ Outgoing request:", config.method?.toUpperCase(), config.baseURL + config.url);
    return config;
  },
  err => Promise.reject(err)
);

/**
 * Injects Authorization or LinkToken headers.
 */
apiClient.interceptors.request.use(cfg => {
  const token = getAccessTokenHandler();
  if (token) {
    cfg.headers.Authorization = `Bearer ${token}`;
  } else if (getLinkTokenHandler()) {
    cfg.headers.LinkToken = getLinkTokenHandler();
  }

  return cfg;
});

/**
 * Proactively refresh token before it's about to expire (< 60s).
 * This avoids the user ever seeing a 401 due to expiry.
 */
apiClient.interceptors.request.use(
  async config => {
    const token = getAccessTokenHandler();
    if (!token) return config;

    try {
      const { exp } = jwtDecode(token);
      const now = Date.now() / 1000;

      if (exp - now < 60) {
        console.info("⏳ Token is about to expire, refreshing…");

        // Queue request while refreshing
        const requestPromise = new Promise(resolve => {
          subscribe(isTokenRefreshed => {
            if (isTokenRefreshed) {
              console.info("✅ Resuming request after refresh");
              resolve(config);
            }
          });
        });

        // Trigger refresh
        refreshTokenRequest()
          .then(success => notifySubscribers(success))
          .catch(() => notifySubscribers(null));

        return requestPromise;
      }

      return config;
    } catch (err) {
      console.warn("⚠️ Could not decode token for proactive check");
      return config;
    }
  },
  err => Promise.reject(err)
);

/**
 * Handles 401 errors by attempting a reactive token refresh.
 * If refresh succeeds, the original request is retried.
 */
apiClient.interceptors.response.use(
  res => res,
  async error => {
    const originalRequest = error.config;

    if (
      error.response?.status === 401 &&
      !originalRequest._retry
    ) {
      originalRequest._retry = true;

      const newToken = await refreshTokenRequest();
      if (newToken) {
        console.info("🔁 Retrying request after token refresh");
        return apiClient(originalRequest);
      }
    }

    return Promise.reject(error);
  }
);

/**
 * Generic request helper used by the app.
 */
export function request(method, url, data = null) {
  return apiClient({ method, url, data });
}

export default apiClient;
