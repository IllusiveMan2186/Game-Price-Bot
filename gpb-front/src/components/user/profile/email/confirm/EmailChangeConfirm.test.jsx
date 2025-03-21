import React from 'react';
import { render } from '@testing-library/react';
import EmailChangeConfirm from './EmailChangeConfirm';
import { useSearchParams } from 'react-router-dom';
import { useEmailActions } from '@hooks/user/useEmailActions';

// Mock the useSearchParams hook.
jest.mock('react-router-dom', () => ({
  useSearchParams: jest.fn(),
}));

// Mock the useEmailActions hook.
jest.mock('@hooks/user/useEmailActions', () => ({
  useEmailActions: jest.fn(),
}));

describe('EmailChangeConfirm', () => {
  it('should calls emailConfirmRequest with the token from search params', () => {
    const token = 'abc-token';
    // Mock useSearchParams to return an object with a get() method.
    useSearchParams.mockReturnValue([{ get: (key) => (key === 'token' ? token : null) }]);
    
    // Create a mock for emailConfirmRequest.
    const emailConfirmRequestMock = jest.fn();
    useEmailActions.mockReturnValue({
      emailConfirmRequest: emailConfirmRequestMock,
    });
    
    // Render the component.
    render(<EmailChangeConfirm />);
    
    // Verify that emailConfirmRequest was called with the correct token.
    expect(emailConfirmRequestMock).toHaveBeenCalledWith(token);
  });
});
