import React from 'react';
import { render, waitFor } from '@testing-library/react';
import SetLink from './SetLink';

// Mock dependencies
jest.mock('react-router-dom', () => ({
  useParams: jest.fn(),
}));
jest.mock('@contexts/NavigationContext', () => ({
  useNavigation: jest.fn(),
}));
jest.mock('@contexts/AuthContext', () => ({
  useAuth: jest.fn(),
}));
jest.mock('@hooks/user/useLinkActions', () => ({
  useLinkActions: jest.fn(),
}));

describe('SetLink', () => {
  let useParamsMock, useNavigationMock, useAuthMock, useLinkActionsMock;
  let navigateMock, accountLinkRequestMock, setLinkTokenMock, isUserAuthMock;

  beforeEach(() => {
    // Get the mocks from our required modules.
    useParamsMock = require('react-router-dom').useParams;
    useNavigationMock = require('@contexts/NavigationContext').useNavigation;
    useAuthMock = require('@contexts/AuthContext').useAuth;
    useLinkActionsMock = require('@hooks/user/useLinkActions').useLinkActions;

    // Create our function mocks.
    navigateMock = jest.fn();
    accountLinkRequestMock = jest.fn();
    setLinkTokenMock = jest.fn();
    isUserAuthMock = jest.fn();

    // Set up the mocks to return our dummy functions.
    useNavigationMock.mockReturnValue(navigateMock);
    useLinkActionsMock.mockReturnValue({
      accountLinkRequest: accountLinkRequestMock,
    });
    useAuthMock.mockReturnValue({
      isUserAuth: isUserAuthMock,
      setLinkToken: setLinkTokenMock,
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  test('calls accountLinkRequest and navigates when user is authenticated', async () => {
    // Simulate URL parameter "token" with value 'my-token'
    useParamsMock.mockReturnValue({ token: 'my-token' });
    // Simulate that the user is authenticated.
    isUserAuthMock.mockReturnValue(true);

    render(<SetLink />);

    // Because the conditional is outside useEffect, it runs immediately.
    expect(accountLinkRequestMock).toHaveBeenCalledWith('my-token');
    expect(setLinkTokenMock).not.toHaveBeenCalled();

    // useEffect should call navigate('/') after mounting.
    await waitFor(() => {
      expect(navigateMock).toHaveBeenCalledWith('/');
    });
  });

  test('calls setLinkToken and navigates when user is not authenticated', async () => {
    // Simulate URL parameter "token" with value 'my-token'
    useParamsMock.mockReturnValue({ token: 'my-token' });
    // Simulate that the user is not authenticated.
    isUserAuthMock.mockReturnValue(false);

    render(<SetLink />);

    // In this case, setLinkToken should be called instead.
    expect(setLinkTokenMock).toHaveBeenCalledWith('my-token');
    expect(accountLinkRequestMock).not.toHaveBeenCalled();

    // Verify that navigate('/') is called in the useEffect.
    await waitFor(() => {
      expect(navigateMock).toHaveBeenCalledWith('/');
    });
  });
});
