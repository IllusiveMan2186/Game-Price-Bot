import React from 'react';
import { render, screen } from '@testing-library/react';
import ActivationPage from './ActivationPage';

jest.mock('react-router-dom', () => ({
  useSearchParams: jest.fn(),
}));

jest.mock('@hooks/user/useActivationActions', () => ({
  useActivationActions: jest.fn(),
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

    const { useActivationActions } = require('@hooks/user/useActivationActions');
    useActivationActions.mockReturnValue({
      activateUserAccountRequest: mockActivateUserAccountRequest,
    });

    render(<ActivationPage />);

    expect(mockActivateUserAccountRequest).toHaveBeenCalledWith(token);
    expect(screen.getByText('Activating your account...')).toBeInTheDocument();
  });
});
