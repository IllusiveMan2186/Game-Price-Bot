jest.mock('@root/config', () => ({
    BACKEND_SERVICE_URL: 'http://localhost/api',
}));

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
        jest.resetModules();
        jest.isolateModules(() => {
            mockApiClient = jest.fn();
            mockApiClient.interceptors = {
                response: { use: jest.fn() },
            };
            mockApiClient.post = jest.fn();

            const axios = require('axios');
            axios.create.mockReturnValue(mockApiClient);

            apiModule = require('./httpService');
            apiClient = apiModule.default;
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

    it('should apiClient has correct default configuration', () => {
        jest.resetModules();
        jest.isolateModules(() => {
            const axios = require('axios');
            const fakeApiClient = {
                interceptors: { response: { use: jest.fn() } },
                post: jest.fn(),
            };
            axios.create.mockReturnValue(fakeApiClient);
            require('./httpService');
            expect(axios.create).toHaveBeenCalledWith({
                baseURL: 'http://localhost/api',
                withCredentials: true,
                headers: { "Content-Type": "application/json" },
            });
        });
    });


    describe('request function', () => {
        it('should sets Authorization header when accessToken is provided', async () => {
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

        it('should sets LinkToken header when accessToken is not provided', async () => {
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
        it('should returns new token on success', async () => {
            mockApiClient.post.mockResolvedValue({ data: 'new-token' });
            const setAccessTokenMock = jest.fn();
            const logoutMock = jest.fn();
            const navigateMock = jest.fn();

            const result = await refreshToken(setAccessTokenMock, logoutMock, navigateMock);
            expect(result).toBe('new-token');
            expect(setAccessTokenMock).toHaveBeenCalledWith('new-token');
        });

        it('should calls logoutRequest and returns null on 401 error', async () => {
            const error = { response: { status: 401 } };
            mockApiClient.post
                .mockRejectedValueOnce(error)
                .mockResolvedValueOnce({ data: {} });

            const setAccessTokenMock = jest.fn();
            const logoutMock = jest.fn();
            const navigateMock = jest.fn();

            const result = await refreshToken(setAccessTokenMock, logoutMock, navigateMock);

            expect(result).toBeNull();
            expect(logoutMock).toHaveBeenCalledWith(navigateMock);
        });


        it('should returns null on error with non-401 status', async () => {
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
        it('should calls logout callback on successful logout', async () => {
            mockApiClient.post.mockResolvedValue({ data: {} });
            const logoutMock = jest.fn();
            const navigateMock = jest.fn();

            await logoutRequest(logoutMock, navigateMock);
            expect(logoutMock).toHaveBeenCalledWith(navigateMock);
        });

        it('should handles error during logout', async () => {
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
        it('should installs a response interceptor', () => {
            setupInterceptors(() => { }, () => { }, () => { });
            expect(mockApiClient.interceptors.response.use).toHaveBeenCalled();
        });

        it('should retries request after refreshing token on 401 error', async () => {
            setupInterceptors(() => { }, () => { }, () => { });
            const originalRequest = { url: '/test', headers: {}, _retry: false };
            const error = { config: originalRequest, response: { status: 401 } };
            const refreshTokenSpy = jest.spyOn(apiModule, 'refreshToken').mockResolvedValue('new-token');
            mockApiClient.mockResolvedValue({ data: 'ok', config: originalRequest });
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
        it('should logs the method and url', async () => {
            const consoleInfoSpy = jest.spyOn(console, 'info').mockImplementation(() => { });
            mockApiClient.mockResolvedValue({ data: {} });

            await request('PUT', '/logging-test', { test: 1 }, null, null);

            expect(consoleInfoSpy).toHaveBeenCalledWith('PUT /logging-test');
            consoleInfoSpy.mockRestore();
        });
    });
});
