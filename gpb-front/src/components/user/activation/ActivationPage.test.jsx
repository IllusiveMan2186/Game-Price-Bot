import React from 'react';
import { render, screen } from '@testing-library/react';
import ActivationPage from './ActivationPage';

jest.mock('react-router-dom', () => ({
  useSearchParams: jest.fn(),
}));

jest.mock('@contexts/NavigationContext', () => ({
  useNavigation: jest.fn(),
}));

jest.mock('@hooks/user/useLinkActions', () => ({
  useLinkActions: jest.fn(),
}));

describe('ActivationPage', () => {
  it('should calls activateUserAccountRequest with token and navigate, and renders activating message', () => {
    const token = 'test-token';
    const mockNavigate = jest.fn();
    const mockActivateUserAccountRequest = jest.fn();

    const { useSearchParams } = require('react-router-dom');
    useSearchParams.mockReturnValue([
      {
        get: (key) => (key === 'token' ? token : null),
      },
    ]);

    const { useNavigation } = require('@contexts/NavigationContext');
    useNavigation.mockReturnValue(mockNavigate);

    const { useLinkActions } = require('@hooks/user/useLinkActions');
    useLinkActions.mockReturnValue({
      activateUserAccountRequest: mockActivateUserAccountRequest,
    });

    render(<ActivationPage />);

    expect(mockActivateUserAccountRequest).toHaveBeenCalledWith(token, mockNavigate);
    expect(screen.getByText('Activating your account...')).toBeInTheDocument();
  });
});
