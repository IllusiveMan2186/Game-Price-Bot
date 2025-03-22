import React from 'react';
import { render, waitFor } from '@testing-library/react';
import { RefreshProvider } from '../RefreshContext';

jest.mock('@contexts/NavigationContext', () => ({
  useNavigation: () => jest.fn(),
}));

jest.mock('@contexts/AuthContext', () => ({
  useAuth: () => ({
    getAccessToken: () => 'dummy-access-token',
    getLinkToken: () => 'dummy-link-token',
    isUserAuth: () => true,
    setAccessToken: jest.fn(),
    logout: jest.fn(),
  }),
}));

jest.mock('@services/httpService', () => ({
  setupInterceptors: jest.fn(),
  refreshToken: jest.fn(),
}));

import { setupInterceptors, refreshToken } from '@services/httpService';

describe('RefreshProvider', () => {
  const fakeNavigate = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should calls setupInterceptors on mount', async () => {
    jest.spyOn(require('@contexts/NavigationContext'), 'useNavigation').mockReturnValue(fakeNavigate);
    jest.spyOn(require('@contexts/AuthContext'), 'useAuth').mockReturnValue({
      isUserAuth: () => true,
      setAccessToken: jest.fn(),
      logout: jest.fn(),
      getAccessToken: () => 'dummy-access-token',
      getLinkToken: () => 'dummy-link-token',
    });

    render(
      <RefreshProvider>
        <div>child content</div>
      </RefreshProvider>
    );

    await waitFor(() => {
      expect(setupInterceptors).toHaveBeenCalledTimes(1);
      expect(refreshToken).not.toHaveBeenCalled();
    });
  });

  it('should calls refreshToken when user is not authenticated', async () => {
    const setAccessTokenMock = jest.fn();
    const logoutMock = jest.fn();
    jest.spyOn(require('@contexts/AuthContext'), 'useAuth').mockReturnValue({
      isUserAuth: () => false,
      setAccessToken: setAccessTokenMock,
      logout: logoutMock,
      getAccessToken: () => null,
      getLinkToken: () => 'dummy-link-token',
    });
    jest.spyOn(require('@contexts/NavigationContext'), 'useNavigation').mockReturnValue(fakeNavigate);

    render(
      <RefreshProvider>
        <div>child content</div>
      </RefreshProvider>
    );

    await waitFor(() => {
      expect(setupInterceptors).toHaveBeenCalledWith(setAccessTokenMock, logoutMock, fakeNavigate);
      expect(refreshToken).toHaveBeenCalledWith(setAccessTokenMock);
    });
  });

  it('should renders children', () => {
    jest.spyOn(require('@contexts/NavigationContext'), 'useNavigation').mockReturnValue(fakeNavigate);
    jest.spyOn(require('@contexts/AuthContext'), 'useAuth').mockReturnValue({
      isUserAuth: () => true,
      setAccessToken: jest.fn(),
      logout: jest.fn(),
      getAccessToken: () => 'dummy-access-token',
      getLinkToken: () => 'dummy-link-token',
    });

    const { getByText } = render(
      <RefreshProvider>
        <div>child content</div>
      </RefreshProvider>
    );
    expect(getByText('child content')).toBeInTheDocument();
  });
});
