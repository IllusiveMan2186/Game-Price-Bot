jest.mock('@root/config', () => ({
    BACKEND_SERVICE_URL: 'http://localhost/api',
}));

jest.mock('axios', () => ({
    create: jest.fn(),
}));

describe('httpService', () => {
    let apiClientMock;
    let noAuthClientMock;
    let request, refreshTokenRequest, logoutRequest, registerAuthHandlers;

    beforeEach(() => {
        jest.resetModules();
        jest.clearAllMocks();

        const axios = require('axios');

        let requestInterceptors = [];

        // Simulate axios instance with request interceptors
        apiClientMock = Object.assign(jest.fn(async config => {
            config.headers = config.headers || {};
            for (const interceptor of requestInterceptors) {
                config = await interceptor(config);
            }
            return { data: 'ok', config };
        }), {
            interceptors: {
                request: {
                    use: fn => requestInterceptors.push(fn),
                },
                response: {
                    use: jest.fn(),
                },
            },
            post: jest.fn(),
            defaults: { headers: { common: {} } },
        });

        noAuthClientMock = {
            post: jest.fn(),
        };

        axios.create
            .mockImplementationOnce(() => apiClientMock)
            .mockImplementationOnce(() => noAuthClientMock);

        jest.isolateModules(() => {
            const httpService = require('./httpService');
            request = httpService.request;
            refreshTokenRequest = httpService.refreshTokenRequest;
            logoutRequest = httpService.logoutRequest;
            registerAuthHandlers = httpService.registerAuthHandlers;
        });
    });

    describe('registerAuthHandlers and refreshTokenRequest', () => {
        it('should set token and update apiClient header on success', async () => {
            noAuthClientMock.post.mockResolvedValue({ data: 'new-token' });
            const setAccessToken = jest.fn();

            registerAuthHandlers({
                getAccessToken: () => null,
                setAccessToken,
                getLinkToken: () => null,
                logout: jest.fn(),
                navigate: jest.fn(),
            });

            const result = await refreshTokenRequest();

            expect(result).toBe(true);
            expect(setAccessToken).toHaveBeenCalledWith('new-token');
            expect(apiClientMock.defaults.headers.common.Authorization).toBe('Bearer new-token');
        });

        it('should call logout and navigate on 401 error', async () => {
            noAuthClientMock.post.mockRejectedValue({ response: { status: 401 } });

            const logout = jest.fn();
            const navigate = jest.fn();

            registerAuthHandlers({
                getAccessToken: () => null,
                setAccessToken: jest.fn(),
                getLinkToken: () => null,
                logout,
                navigate,
            });

            const result = await refreshTokenRequest();

            expect(result).toBe(false);
            expect(logout).toHaveBeenCalled();
            expect(navigate).toHaveBeenCalled();
        });

        it('should not call logout on non-401 error', async () => {
            noAuthClientMock.post.mockRejectedValue({ response: { status: 500 } });

            const logout = jest.fn();
            const navigate = jest.fn();

            registerAuthHandlers({
                getAccessToken: () => null,
                setAccessToken: jest.fn(),
                getLinkToken: () => null,
                logout,
                navigate,
            });

            const result = await refreshTokenRequest();

            expect(result).toBe(false);
            expect(logout).not.toHaveBeenCalled();
            expect(navigate).not.toHaveBeenCalled();
        });
    });

    describe('logoutRequest', () => {
        it('should call logout and navigate on successful logout', async () => {
            noAuthClientMock.post.mockResolvedValue({});

            const logout = jest.fn();
            const navigate = jest.fn();

            registerAuthHandlers({
                getAccessToken: () => null,
                setAccessToken: jest.fn(),
                getLinkToken: () => null,
                logout,
                navigate,
            });

            await logoutRequest();

            expect(noAuthClientMock.post).toHaveBeenCalledWith('/logout-user');
            expect(logout).toHaveBeenCalled();
            expect(navigate).toHaveBeenCalled();
        });

        it('should handle logout error and still call logout + navigate', async () => {
            const consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => { });
            noAuthClientMock.post.mockRejectedValue(new Error('fail'));

            const logout = jest.fn();
            const navigate = jest.fn();

            registerAuthHandlers({
                getAccessToken: () => null,
                setAccessToken: jest.fn(),
                getLinkToken: () => null,
                logout,
                navigate,
            });

            await logoutRequest();

            expect(consoleSpy).toHaveBeenCalledWith('Logout error:', expect.any(Error));
            expect(logout).toHaveBeenCalled();
            expect(navigate).toHaveBeenCalled();
            consoleSpy.mockRestore();
        });
    });

    describe('proactive token refresh interceptor', () => {
        let jwtDecodeMock;
        let nowInSeconds = 100000;

        beforeEach(() => {
            jest.resetModules();
            jest.clearAllMocks();

            // Mock jwt-decode before httpService is imported
            jest.mock('jwt-decode', () => ({
                jwtDecode: jest.fn(),
            }));

            // Prepare axios mock with interceptors and manual call-through
            const axios = require('axios');
            let requestInterceptors = [];

            apiClientMock = Object.assign(jest.fn(async config => {
                config.headers = config.headers || {};
                for (const interceptor of requestInterceptors) {
                    config = await interceptor(config);
                }
                return { data: 'ok', config };
            }), {
                interceptors: {
                    request: {
                        use: fn => requestInterceptors.push(fn),
                    },
                    response: {
                        use: jest.fn(),
                    },
                },
                post: jest.fn(),
                defaults: { headers: { common: {} } },
            });

            noAuthClientMock = {
                post: jest.fn(),
            };

            axios.create
                .mockImplementationOnce(() => apiClientMock)
                .mockImplementationOnce(() => noAuthClientMock);
        });

        it('should proactively refresh token if it expires in less than 60s (success case)', async () => {
            const { jwtDecode } = require('jwt-decode');
            jwtDecode.mockReturnValue({ exp: nowInSeconds + 30 });
            jest.spyOn(Date, 'now').mockReturnValue(nowInSeconds * 1000);

            jest.isolateModules(() => {
                const httpService = require('./httpService');
                jwtDecodeMock = jwtDecode;

                // Fake successful refresh behavior
                httpService.refreshTokenRequest = jest.fn().mockImplementation(async () => {
                    httpService.registerAuthHandlers({
                        getAccessToken: () => 'mocked-token',
                        setAccessToken: jest.fn(),
                        getLinkToken: () => null,
                        logout: jest.fn(),
                        navigate: jest.fn(),
                    });

                    httpService.default.defaults.headers.common.Authorization = 'Bearer mocked-token';
                    return true;
                });

                httpService.registerAuthHandlers({
                    getAccessToken: () => 'token123',
                    setAccessToken: jest.fn(),
                    getLinkToken: () => null,
                    logout: jest.fn(),
                    navigate: jest.fn(),
                });

                return httpService.request('GET', '/near-expire', {}).then(result => {
                    expect(httpService.refreshTokenRequest).toHaveBeenCalled();
                    expect(result.config.url).toBe('/near-expire');
                });
            });

            Date.now.mockRestore();
        });

        it('should try to refresh but not retry if refresh fails', async () => {
            const { jwtDecode } = require('jwt-decode');
            jwtDecode.mockReturnValue({ exp: nowInSeconds + 30 });
            jest.spyOn(Date, 'now').mockReturnValue(nowInSeconds * 1000);

            jest.isolateModules(() => {
                const httpService = require('./httpService');
                jwtDecodeMock = jwtDecode;

                // Simulate failed refresh
                httpService.refreshTokenRequest = jest.fn().mockImplementation(async () => false);

                httpService.registerAuthHandlers({
                    getAccessToken: () => 'token456',
                    setAccessToken: jest.fn(),
                    getLinkToken: () => null,
                    logout: jest.fn(),
                    navigate: jest.fn(),
                });

                return httpService.request('GET', '/fail-refresh', {}).then(result => {
                    expect(httpService.refreshTokenRequest).toHaveBeenCalled();
                    expect(result.config.url).toBe('/fail-refresh');
                });
            });

            Date.now.mockRestore();
        });
    });

    describe('reactive token refresh interceptor', () => {
        let responseInterceptor;
        let retryMock;

        beforeEach(() => {
            jest.resetModules();
            jest.clearAllMocks();

            const axios = require('axios');

            const requestInterceptors = [];
            const responseInterceptors = {
                use: jest.fn((success, error) => {
                    responseInterceptor = error;
                }),
            };

            apiClientMock = Object.assign(jest.fn(), {
                interceptors: {
                    request: {
                        use: jest.fn(fn => requestInterceptors.push(fn)),
                    },
                    response: responseInterceptors,
                },
                defaults: { headers: { common: {} } },
            });

            noAuthClientMock = {
                // ✅ Return valid data shape by default
                post: jest.fn().mockResolvedValue({ data: 'mocked-token' }),
            };

            axios.create
                .mockImplementationOnce(() => apiClientMock) // for apiClient
                .mockImplementationOnce(() => noAuthClientMock); // for noAuthClient
        });

        it('should refresh and retry request on 401 (first time)', async () => {
            jest.isolateModules(() => {
                const httpService = require('./httpService');

                const error = {
                    response: { status: 401 },
                    config: { _retry: false, url: '/secure' },
                };

                // ✅ Simulate refresh returns { data: 'mocked-token' }
                noAuthClientMock.post.mockResolvedValueOnce({ data: 'mocked-token' });

                // ✅ Simulate retry via apiClient (which is apiClientMock)
                apiClientMock.mockResolvedValueOnce({ data: 'retried' });

                return responseInterceptor(error).then(res => {
                    expect(noAuthClientMock.post).toHaveBeenCalledWith('/refresh-token');
                    expect(apiClientMock).toHaveBeenCalledWith({ ...error.config, _retry: true });
                    expect(res.data).toBe('retried');
                });
            });
        });


        it('should not retry if refresh fails (401)', async () => {
            // Override only for this test
            noAuthClientMock.post.mockRejectedValueOnce({ response: { status: 401 } });

            jest.isolateModules(() => {
                const httpService = require('./httpService');

                const error = {
                    response: { status: 401 },
                    config: { _retry: false, url: '/secure' },
                };

                return responseInterceptor(error)
                    .then(() => {
                        throw new Error('Expected to reject');
                    })
                    .catch(err => {
                        expect(noAuthClientMock.post).toHaveBeenCalledWith('/refresh-token');
                        expect(err).toBe(error);
                    });
            });
        });

        it('should not retry if request already retried (_retry === true)', async () => {
            jest.isolateModules(() => {
                const httpService = require('./httpService');

                const error = {
                    response: { status: 401 },
                    config: { _retry: true, url: '/secure' },
                };

                return responseInterceptor(error)
                    .then(() => {
                        throw new Error('Expected to reject');
                    })
                    .catch(err => {
                        expect(noAuthClientMock.post).not.toHaveBeenCalled();
                        expect(err).toBe(error);
                    });
            });
        });
    });



    describe('request', () => {
        it('should send request with Authorization header when token exists', async () => {
            registerAuthHandlers({
                getAccessToken: () => 'token-abc',
                setAccessToken: jest.fn(),
                getLinkToken: () => null,
                logout: jest.fn(),
                navigate: jest.fn(),
            });

            const result = await request('GET', '/test', { foo: 1 });

            expect(result.data).toBe('ok');
            expect(result.config.headers.Authorization).toBe('Bearer token-abc');
        });

        it('should send request with LinkToken header when no access token', async () => {
            registerAuthHandlers({
                getAccessToken: () => null,
                setAccessToken: jest.fn(),
                getLinkToken: () => 'link-xyz',
                logout: jest.fn(),
                navigate: jest.fn(),
            });

            const result = await request('POST', '/link-endpoint', { bar: 2 });

            expect(result.data).toBe('ok');
            expect(result.config.headers.LinkToken).toBe('link-xyz');
        });
    });
});
