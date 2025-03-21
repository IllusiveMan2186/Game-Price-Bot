import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import LoginForm from './LoginForm';

// Mock the Message component with ES module interop so that it returns the passed string
jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <span>{string}</span>
}));

// Mock the hooks
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
    // Create fresh mocks for each test
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

  test('submits the form and calls loginRequest with correct values', async () => {
    render(<LoginForm />);

    // Get the email and password input fields using the "name" attribute
    const emailInput = document.querySelector('input[name="email"]');
    const passwordInput = document.querySelector('input[name="password"]');

    // Simulate typing in the form fields
    fireEvent.change(emailInput, { target: { value: 'user@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });

    // Find and click the submit button (its text is provided by Message)
    const submitButton = screen.getByRole('button', { name: /app.login.form.singup/i });
    fireEvent.click(submitButton);

    // Wait for the loginRequest function to be called
    await waitFor(() => {
      expect(loginRequestMock).toHaveBeenCalled();
    });

    // Verify that loginRequest was called with email, password, and a callback function
    const callArgs = loginRequestMock.mock.calls[0];
    expect(callArgs[0]).toBe('user@example.com');
    expect(callArgs[1]).toBe('password123');
    expect(typeof callArgs[2]).toBe('function');
  });

  test('displays resend link and calls resendActivationEmailRequest on click when account is not activated', async () => {
    // Simulate loginRequest setting an error message indicating the account is not activated.
    loginRequestMock.mockImplementation((email, password, setErrorMessage) => {
      setErrorMessage("app.user.error.account.not.activated");
    });

    render(<LoginForm />);

    // Fill in the email and password fields
    const emailInput = document.querySelector('input[name="email"]');
    const passwordInput = document.querySelector('input[name="password"]');
    fireEvent.change(emailInput, { target: { value: 'inactive@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'password456' } });

    // Submit the form
    const submitButton = screen.getByRole('button', { name: /app.login.form.singup/i });
    fireEvent.click(submitButton);

    // Wait for loginRequest to be called and state update
    await waitFor(() => {
      expect(loginRequestMock).toHaveBeenCalled();
    });

    // The error message should trigger the rendering of the resend link.
    const resendLink = screen.getByText(/app.user.error.account.not.activated.send/i);
    expect(resendLink).toBeInTheDocument();

    // Click the resend link
    fireEvent.click(resendLink);

    // Verify that resendActivationEmailRequest was called with the confirmed email.
    expect(resendActivationEmailRequestMock).toHaveBeenCalledWith('inactive@example.com');
  });
});
