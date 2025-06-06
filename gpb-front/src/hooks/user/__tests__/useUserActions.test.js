import { renderHook } from '@testing-library/react';
import { useUserActions } from '../useUserActions';
import { useHttpHelper } from '@hooks/useHttpHelper';
import { useNavigation } from '@contexts/NavigationContext';

jest.mock('@hooks/useHttpHelper');
jest.mock('@contexts/NavigationContext');
jest.mock('react-notifications');

describe('useUserActions', () => {
  const handleRequestMock = jest.fn();
  const navigateMock = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();

    useHttpHelper.mockReturnValue({ handleRequest: handleRequestMock });
    useNavigation.mockReturnValue(navigateMock);
  });

  it('should emailChangeRequest calls handleRequest with correct arguments', () => {
    const { result } = renderHook(() => useUserActions());
    const setErrorMock = jest.fn();

    result.current.emailChangeRequest('test@example.com', setErrorMock);

    expect(handleRequestMock).toHaveBeenCalledWith(
      'PATCH',
      '/email',
      { email: 'test@example.com' },
      expect.any(Function),
      setErrorMock
    );
  });

  it('should passwordChangeRequest calls handleRequest with correct arguments', () => {
    const { result } = renderHook(() => useUserActions());
    const setErrorMock = jest.fn();
    const logoutMock = jest.fn();

    result.current.passwordChangeRequest('oldPass', 'newPass', setErrorMock, logoutMock);

    expect(handleRequestMock).toHaveBeenCalledWith(
      'PATCH',
      '/user/password',
      { oldPassword: 'oldPass', newPassword: 'newPass' },
      expect.any(Function),
      setErrorMock
    );
  });

  it('should localeChangeRequest calls handleRequest with correct arguments', () => {
    const { result } = renderHook(() => useUserActions());

    result.current.localeChangeRequest('en');

    expect(handleRequestMock).toHaveBeenCalledWith(
      'PATCH',
      '/user/locale',
      { locale: 'en' },
      expect.any(Function),
      expect.any(Function)
    );
  });

  it('should checkAuthRequest resolves to true on status 200', async () => {
    const { result } = renderHook(() => useUserActions());

    handleRequestMock.mockImplementation((_, __, ___, success) => {
      success({ status: 200 });
    });

    await expect(result.current.checkAuthRequest()).resolves.toBe(true);
  });

  it('should checkAuthRequest resolves to false on non-200 status', async () => {
    const { result } = renderHook(() => useUserActions());

    handleRequestMock.mockImplementation((_, __, ___, success) => {
      success({ status: 401 });
    });

    await expect(result.current.checkAuthRequest()).resolves.toBe(false);
    expect(window.localStorage.getItem('IS_AUTHENTICATED')).toBeNull();
  });

  it('should checkAuthRequest resolves to false on error', async () => {
    const { result } = renderHook(() => useUserActions());

    handleRequestMock.mockImplementation((_, __, ___, ____, error) => {
      error();
    });

    await expect(result.current.checkAuthRequest()).resolves.toBe(false);
    expect(window.localStorage.getItem('IS_AUTHENTICATED')).toBeNull();
  });
});