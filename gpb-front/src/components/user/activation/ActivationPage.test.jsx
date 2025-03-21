import React from 'react';
import { render, screen } from '@testing-library/react';
import ActivationPage from './ActivationPage';

// Mock the react-router-dom hook
jest.mock('react-router-dom', () => ({
  useSearchParams: jest.fn(),
}));

// Mock the Navigation context hook
jest.mock('@contexts/NavigationContext', () => ({
  useNavigation: jest.fn(),
}));

// Mock the link actions hook
jest.mock('@hooks/user/useLinkActions', () => ({
  useLinkActions: jest.fn(),
}));

describe('ActivationPage', () => {
  test('calls activateUserAccountRequest with token and navigate, and renders activating message', () => {
    const token = 'test-token';
    const mockNavigate = jest.fn();
    const mockActivateUserAccountRequest = jest.fn();

    // Setup useSearchParams to return a token in the URL
    const { useSearchParams } = require('react-router-dom');
    useSearchParams.mockReturnValue([
      {
        get: (key) => (key === 'token' ? token : null),
      },
    ]);

    // Setup useNavigation to return a mock navigation function
    const { useNavigation } = require('@contexts/NavigationContext');
    useNavigation.mockReturnValue(mockNavigate);

    // Setup useLinkActions to return our mock activateUserAccountRequest
    const { useLinkActions } = require('@hooks/user/useLinkActions');
    useLinkActions.mockReturnValue({
      activateUserAccountRequest: mockActivateUserAccountRequest,
    });

    render(<ActivationPage />);

    // Verify that activateUserAccountRequest was called with the token and navigate function.
    expect(mockActivateUserAccountRequest).toHaveBeenCalledWith(token, mockNavigate);
    // Verify that the activating message is rendered.
    expect(screen.getByText('Activating your account...')).toBeInTheDocument();
  });
});
