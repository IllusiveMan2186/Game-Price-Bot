// RefreshProvider.test.js
import React from 'react';
import { render, waitFor } from '@testing-library/react';
import { RefreshProvider } from '../RefreshContext';

// Set up mocks for dependencies.
jest.mock('@contexts/NavigationContext', () => ({
  useNavigation: () => jest.fn(), // we'll override in tests if needed
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

// Import our mocks so we can inspect them.
import { setupInterceptors, refreshToken } from '@services/httpService';

describe('RefreshProvider', () => {
  // We'll use a custom navigate function for tests.
  const fakeNavigate = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('calls setupInterceptors on mount', async () => {
    // Configure useNavigation and useAuth for an authenticated user.
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
      // When authenticated, refreshToken should NOT be called.
      expect(refreshToken).not.toHaveBeenCalled();
    });
  });

  test('calls refreshToken when user is not authenticated', async () => {
    const setAccessTokenMock = jest.fn();
    const logoutMock = jest.fn();
    // Override useAuth to simulate a non-authenticated user.
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
      // setupInterceptors is always called.
      expect(setupInterceptors).toHaveBeenCalledWith(setAccessTokenMock, logoutMock, fakeNavigate);
      // When not authenticated, refreshToken should be called with setAccessToken.
      expect(refreshToken).toHaveBeenCalledWith(setAccessTokenMock);
    });
  });

  test('renders children', () => {
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
