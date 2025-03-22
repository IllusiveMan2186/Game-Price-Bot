import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import LoginForm from './LoginForm';

jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <span>{string}</span>
}));

jest.mock('@hooks/user/useAuthActions', () => ({
  useAuthActions: jest.fn(),
}));

jest.mock('@hooks/user/useActivationActions', () => ({
  useActivationActions: jest.fn(),
}));

describe('LoginForm', () => {
  let loginRequestMock;
  let resendActivationEmailRequestMock;

  beforeEach(() => {
    loginRequestMock = jest.fn();
    resendActivationEmailRequestMock = jest.fn();

    const { useAuthActions } = require('@hooks/user/useAuthActions');
    useAuthActions.mockReturnValue({
      loginRequest: loginRequestMock,
    });

    const { useActivationActions } = require('@hooks/user/useActivationActions');
    useActivationActions.mockReturnValue({
      resendActivationEmailRequest: resendActivationEmailRequestMock,
    });
  });

  it('should submits the form and calls loginRequest with correct values', async () => {
    render(<LoginForm />);

    const emailInput = document.querySelector('input[name="email"]');
    const passwordInput = document.querySelector('input[name="password"]');

    fireEvent.change(emailInput, { target: { value: 'user@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });

    const submitButton = screen.getByRole('button', { name: /app.login.form.singup/i });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(loginRequestMock).toHaveBeenCalled();
    });

    const callArgs = loginRequestMock.mock.calls[0];
    expect(callArgs[0]).toBe('user@example.com');
    expect(callArgs[1]).toBe('password123');
    expect(typeof callArgs[2]).toBe('function');
  });

  it('should displays resend link and calls resendActivationEmailRequest on click when account is not activated', async () => {
    loginRequestMock.mockImplementation((email, password, setErrorMessage) => {
      setErrorMessage("app.user.error.account.not.activated");
    });

    render(<LoginForm />);

    const emailInput = document.querySelector('input[name="email"]');
    const passwordInput = document.querySelector('input[name="password"]');
    fireEvent.change(emailInput, { target: { value: 'inactive@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password456' } });

    const submitButton = screen.getByRole('button', { name: /app.login.form.singup/i });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(loginRequestMock).toHaveBeenCalled();
    });

    const resendLink = screen.getByText(/app.user.error.account.not.activated.send/i);
    expect(resendLink).toBeInTheDocument();

    fireEvent.click(resendLink);

    expect(resendActivationEmailRequestMock).toHaveBeenCalledWith('inactive@example.com');
  });
});
