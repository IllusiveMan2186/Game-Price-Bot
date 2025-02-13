import axios from "axios";
import config from "@root/config";

// Create a custom axios instance with default settings.
const apiClient = axios.create({
  baseURL: config.BACKEND_SERVICE_URL,
  withCredentials: true,
  headers: {
    "Content-Type": "application/json",
  },
});

// Variables to manage token refresh state.
let isRefreshing = false;
let refreshSubscribers = [];

/**
 * Subscribes a callback to be notified when the token is refreshed.
 * @param {Function} cb - The callback function.
 */
const subscribeTokenRefresh = (cb) => {
  refreshSubscribers.push(cb);
};

/**
 * Notifies all subscribers with the new token.
 * @param {string|null} newToken - The refreshed access token.
 */
const onTokenRefreshed = (newToken) => {
  refreshSubscribers.forEach((cb) => cb(newToken));
  refreshSubscribers = [];
};

/**
 * Attempts to refresh the access token.
 * @param {Function} setAccessToken - Function to update the access token.
 * @param {Function} logout - Function to log out the user.
 * @returns {Promise<string|null>} The new access token or null if refresh failed.
 */
export const refreshToken = async (setAccessToken, logout) => {
  try {
    const response = await apiClient.post("/refresh-token");
    const newAccessToken = response.data;
    setAccessToken(newAccessToken);
    onTokenRefreshed(newAccessToken);
    return newAccessToken;
  } catch (error) {
    console.error("Failed to refresh token:", error);

    if (error.response && error.response.status === 401) {
      console.info("Refresh token failed with 401, logging out...");
      await logoutRequest(logout);
    } else {
      // Notify all subscribers that the token refresh failed.
      onTokenRefreshed(null);
    }
    return null;
  }
};

/**
 * Logs out the user by calling the logout endpoint.
 * @param {Function} logout - Function to perform logout.
 */
export const logoutRequest = async (logout) => {
  try {
    await apiClient.post("/logout-user");
    logout();
  } catch (error) {
    console.error("Failed to logout:", error);
  }
};

/**
 * Sets up an Axios response interceptor to handle token refreshing.
 * @param {Function} setAccessToken - Function to update the access token.
 * @param {Function} logout - Function to log out the user.
 */
export const setupInterceptors = (setAccessToken, logout) => {
  apiClient.interceptors.response.use(
    (response) => response, // Return successful responses as-is.
    async (error) => {
      const originalRequest = error.config;

      // Handle network errors where error.response might be undefined.
      if (!error.response) {
        console.error("Network or CORS error occurred", error);
        return Promise.reject(error);
      }

      // If the refresh-token endpoint fails with 401, force logout.
      if (
        originalRequest &&
        originalRequest.url === "/refresh-token" &&
        error.response.status === 401
      ) {
        await logoutRequest(logout);
        return Promise.reject(error);
      }

      // If a 401 error is encountered and we haven't retried this request yet.
      if (error.response.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        if (!isRefreshing) {
          isRefreshing = true;
          const newToken = await refreshToken(setAccessToken, logout);
          isRefreshing = false;

          if (newToken) {
            originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
            return apiClient(originalRequest);
          }
          // If token refresh failed, reject the request.
          return Promise.reject(error);
        }

        // If another request is already refreshing, queue this request.
        return new Promise((resolve, reject) => {
          subscribeTokenRefresh((token) => {
            if (token) {
              originalRequest.headers["Authorization"] = `Bearer ${token}`;
              resolve(apiClient(originalRequest));
            } else {
              reject(error);
            }
          });
        });
      }

      return Promise.reject(error);
    }
  );
};

/**
 * Sends an HTTP request using the custom Axios instance.
 * @param {string} method - HTTP method (e.g., GET, POST).
 * @param {string} url - Endpoint URL.
 * @param {object} data - Request payload.
 * @param {string|null} accessToken - Access token for authorization.
 * @param {string|null} linkToken - Alternative token for authorization.
 * @returns {Promise} The Axios request promise.
 */
export const request = (method, url, data, accessToken, linkToken) => {
  const headers = {};

  if (accessToken) {
    headers["Authorization"] = `Bearer ${accessToken}`;
  } else if (linkToken) {
    headers["LinkToken"] = linkToken;
  }

  console.info(`${method} ${url}`);

  return apiClient({
    method,
    url,
    data,
    headers,
  });
};

export default apiClient;
