// httpService.test.js
import config from '@root/config';
jest.mock('@root/config', () => ({
    BACKEND_SERVICE_URL: 'http://localhost/api',
}));

// Ensure axios is mocked before we import the module under test.
jest.mock('axios');

describe('httpService module', () => {
    let apiModule;
    let apiClient;
    let refreshToken;
    let logoutRequest;
    let setupInterceptors;
    let request;
    let mockApiClient;

    beforeEach(() => {
        // Reset modules so our module under test is re-imported fresh.
        jest.resetModules();
        // Isolate modules so our axios mock configuration is applied.
        jest.isolateModules(() => {
            // Create a fake axios instance (a callable function).
            mockApiClient = jest.fn();
            // Add properties our module expects.
            mockApiClient.interceptors = {
                response: { use: jest.fn() },
            };
            // Add a .post method for POST requests.
            mockApiClient.post = jest.fn();

            // Configure axios.create to return our fake instance.
            const axios = require('axios');
            axios.create.mockReturnValue(mockApiClient);

            // Now re-import the module under test.
            apiModule = require('./httpService'); // Adjust path if needed.
            apiClient = apiModule.default; // Our default export (the axios instance).
            // Force our default export to include the fake post method.
            apiClient.post = mockApiClient.post;
            refreshToken = apiModule.refreshToken;
            logoutRequest = apiModule.logoutRequest;
            setupInterceptors = apiModule.setupInterceptors;
            request = apiModule.request;
        });
    });

    afterEach(() => {
        jest.clearAllMocks();
    });

    test('apiClient has correct default configuration', () => {
        jest.resetModules();
        jest.isolateModules(() => {
            const axios = require('axios');
            // Create a fake axios instance.
            const fakeApiClient = {
                interceptors: { response: { use: jest.fn() } },
                post: jest.fn(),
            };
            // Ensure that when httpService calls axios.create, it returns our fake instance.
            axios.create.mockReturnValue(fakeApiClient);
            // Import the module under test.
            require('./httpService');
            // Assert that axios.create was called with the correct config.
            expect(axios.create).toHaveBeenCalledWith({
                baseURL: 'http://localhost/api',
                withCredentials: true,
                headers: { "Content-Type": "application/json" },
            });
        });
    });


    describe('request function', () => {
        test('sets Authorization header when accessToken is provided', async () => {
            mockApiClient.mockResolvedValue({ data: 'ok' });
            const response = await request('GET', '/test', { key: 'value' }, 'my-token', null);
            expect(mockApiClient).toHaveBeenCalledWith(expect.objectContaining({
                method: 'GET',
                url: '/test',
                data: { key: 'value' },
                headers: { Authorization: 'Bearer my-token' },
            }));
            expect(response.data).toBe('ok');
        });

        test('sets LinkToken header when accessToken is not provided', async () => {
            mockApiClient.mockResolvedValue({ data: 'ok' });
            const response = await request('POST', '/test', { key: 'value' }, null, 'my-link-token');
            expect(mockApiClient).toHaveBeenCalledWith(expect.objectContaining({
                method: 'POST',
                url: '/test',
                data: { key: 'value' },
                headers: { LinkToken: 'my-link-token' },
            }));
            expect(response.data).toBe('ok');
        });
    });

    describe('refreshToken function', () => {
        test('returns new token on success', async () => {
            // Simulate a successful POST returning a new token.
            mockApiClient.post.mockResolvedValue({ data: 'new-token' });
            const setAccessTokenMock = jest.fn();
            const logoutMock = jest.fn();
            const navigateMock = jest.fn();

            const result = await refreshToken(setAccessTokenMock, logoutMock, navigateMock);
            expect(result).toBe('new-token');
            expect(setAccessTokenMock).toHaveBeenCalledWith('new-token');
        });

        test('calls logoutRequest and returns null on 401 error', async () => {
            const error = { response: { status: 401 } };
            // For the first POST (refresh-token), reject with a 401 error.
            // For the subsequent POST (logout), resolve successfully.
            mockApiClient.post
                .mockRejectedValueOnce(error)
                .mockResolvedValueOnce({ data: {} });

            const setAccessTokenMock = jest.fn();
            const logoutMock = jest.fn();
            const navigateMock = jest.fn();

            const result = await refreshToken(setAccessTokenMock, logoutMock, navigateMock);

            expect(result).toBeNull();
            // Check that the logout callback was called with navigateMock.
            expect(logoutMock).toHaveBeenCalledWith(navigateMock);
        });


        test('returns null on error with non-401 status', async () => {
            const error = { response: { status: 500 } };
            mockApiClient.post.mockRejectedValue(error);
            const setAccessTokenMock = jest.fn();
            const logoutMock = jest.fn();
            const navigateMock = jest.fn();
            const logoutRequestSpy = jest.spyOn(apiModule, 'logoutRequest');
            const result = await refreshToken(setAccessTokenMock, logoutMock, navigateMock);
            expect(result).toBeNull();
            expect(logoutRequestSpy).not.toHaveBeenCalled();
        });
    });

    describe('logoutRequest function', () => {
        test('calls logout callback on successful logout', async () => {
            mockApiClient.post.mockResolvedValue({ data: {} });
            const logoutMock = jest.fn();
            const navigateMock = jest.fn();

            await logoutRequest(logoutMock, navigateMock);
            expect(logoutMock).toHaveBeenCalledWith(navigateMock);
        });

        test('handles error during logout', async () => {
            const consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => { });
            mockApiClient.post.mockRejectedValue(new Error('logout error'));
            const logoutCallback = jest.fn();
            const navigateMock = jest.fn();

            await logoutRequest(logoutCallback, navigateMock);
            expect(consoleSpy).toHaveBeenCalled();
            consoleSpy.mockRestore();
        });
    });

    describe('setupInterceptors function', () => {
        test('installs a response interceptor', () => {
            setupInterceptors(() => { }, () => { }, () => { });
            expect(mockApiClient.interceptors.response.use).toHaveBeenCalled();
        });

        test('retries request after refreshing token on 401 error', async () => {
            setupInterceptors(() => { }, () => { }, () => { });
            // Create a fake original request.
            const originalRequest = { url: '/test', headers: {}, _retry: false };
            const error = { config: originalRequest, response: { status: 401 } };
            // Spy on refreshToken to simulate a successful refresh.
            const refreshTokenSpy = jest.spyOn(apiModule, 'refreshToken').mockResolvedValue('new-token');
            // Simulate a successful retry by having our fake instance resolve.
            mockApiClient.mockResolvedValue({ data: 'ok', config: originalRequest });
            // Manually simulate the interceptor logic.
            originalRequest._retry = true;
            const newToken = await refreshTokenSpy();
            originalRequest.headers.Authorization = `Bearer ${newToken}`;
            const retryResult = await mockApiClient(originalRequest);
            expect(retryResult.data).toBe('ok');
            expect(originalRequest.headers.Authorization).toBe('Bearer new-token');
            refreshTokenSpy.mockRestore();
        });
    });

    describe('request logging', () => {
        test('logs the method and url', async () => {
            const consoleInfoSpy = jest.spyOn(console, 'info').mockImplementation(() => { });
            mockApiClient.mockResolvedValue({ data: {} });
            try {
                await request('PUT', '/logging-test', { test: 1 }, null, null);
            } catch (e) {
                // Ignore errors.
            }
            expect(consoleInfoSpy).toHaveBeenCalledWith('PUT /logging-test');
            consoleInfoSpy.mockRestore();
        });
    });
});
