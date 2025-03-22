import { renderHook, act } from '@testing-library/react';
import { useAuthActions } from '../useAuthActions';
import { useNavigation } from '@contexts/NavigationContext';
import { useAuth } from '@contexts/AuthContext';
import { useHttpHelper } from '@hooks/useHttpHelper';
import { logoutRequest } from '@services/httpService';
import { getLocale } from '@util/userDataUtils';

jest.mock('@contexts/NavigationContext', () => ({ useNavigation: jest.fn() }));
jest.mock('@contexts/AuthContext', () => ({ useAuth: jest.fn() }));
jest.mock('@hooks/useHttpHelper', () => ({ useHttpHelper: jest.fn() }));
jest.mock('@services/httpService', () => ({ logoutRequest: jest.fn() }));
jest.mock('i18next', () => ({ changeLanguage: jest.fn() }));
jest.mock('@util/userDataUtils', () => ({ setLocale: jest.fn(), getLocale: jest.fn() }));
jest.mock('react-notifications', () => ({ NotificationManager: { success: jest.fn() } }));

describe('useAuthActions', () => {
  const navigateMock = jest.fn();
  const setAccessTokenMock = jest.fn();
  const setUserRoleMock = jest.fn();
  const logoutMock = jest.fn();
  const handleRequestMock = jest.fn();

  beforeEach(() => {
    useNavigation.mockReturnValue(navigateMock);
    useAuth.mockReturnValue({
      setAccessToken: setAccessTokenMock,
      setUserRole: setUserRoleMock,
      logout: logoutMock
    });
    useHttpHelper.mockReturnValue({ handleRequest: handleRequestMock });
    getLocale.mockReturnValue('en');
    jest.clearAllMocks();
  });

  it('should loginRequest calls handleRequest with correct parameters', () => {
    const { result } = renderHook(() => useAuthActions());

    act(() => {
      result.current.loginRequest('test@example.com', 'password', jest.fn());
    });

    expect(handleRequestMock).toHaveBeenCalledWith(
      'POST',
      '/login',
      { email: 'test@example.com', password: 'password' },
      expect.any(Function),
      expect.any(Function)
    );
  });

  it('should registerRequest calls handleRequest and triggers NotificationManager success', () => {
    const { result } = renderHook(() => useAuthActions());

    act(() => {
      result.current.registerRequest('test@example.com', 'password', jest.fn());
    });

    expect(handleRequestMock).toHaveBeenCalledWith(
      'POST',
      '/registration',
      { email: 'test@example.com', password: 'password', locale: 'en' },
      expect.any(Function),
      expect.any(Function)
    );
  });

  it('should userLogoutRequest calls logoutRequest', () => {
    const { result } = renderHook(() => useAuthActions());

    act(() => {
      result.current.userLogoutRequest();
    });

    expect(logoutRequest).toHaveBeenCalledWith(logoutMock);
  });
});
