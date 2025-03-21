import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import RegistrationForm from './RegistrationForm';

// Mock Message component with proper ES Module interop so that it returns the provided string.
jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <span>{string}</span>
}));

// Mock hooks for navigation and auth actions.
jest.mock('@contexts/NavigationContext', () => ({
  useNavigation: jest.fn(),
}));

jest.mock('@hooks/user/useAuthActions', () => ({
  useAuthActions: jest.fn(),
}));

describe('RegistrationForm', () => {
  let registerRequestMock;
  let mockNavigate;

  beforeEach(() => {
    // Reset mocks before each test.
    registerRequestMock = jest.fn();
    mockNavigate = jest.fn();

    const { useAuthActions } = require('@hooks/user/useAuthActions');
    useAuthActions.mockReturnValue({
      registerRequest: registerRequestMock,
    });

    const { useNavigation } = require('@contexts/NavigationContext');
    useNavigation.mockReturnValue(mockNavigate);
  });

  test('submits valid data calling registerRequest', async () => {
    render(<RegistrationForm />);

    // Query input fields by their name attributes.
    const emailInput = document.querySelector('input[name="email"]');
    const passwordInput = document.querySelector('input[name="password"]');
    const confirmPasswordInput = document.querySelector('input[name="confirmPassword"]');
    const submitButton = screen.getByRole('button', { name: /app.registr.form.reg.buttom/i });

    // Fill in valid data:
    // Use a valid email and a password that satisfies all the Yup constraints:
    // - At least 8 characters
    // - Contains uppercase, lowercase, digit, and special character.
    const validEmail = 'test@example.com';
    const validPassword = 'Abcdef1!'; // Satisfies all password rules.
    fireEvent.change(emailInput, { target: { value: validEmail } });
    fireEvent.change(passwordInput, { target: { value: validPassword } });
    fireEvent.change(confirmPasswordInput, { target: { value: validPassword } });

    // Wait for Formik to update the form state; now the button should be enabled.
    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });

    // Submit the form.
    fireEvent.click(submitButton);

    // Verify that registerRequest was called with the correct arguments:
    // email, password, a callback function (setErrorMessage), and navigate.
    await waitFor(() => {
      expect(registerRequestMock).toHaveBeenCalled();
    });

    const callArgs = registerRequestMock.mock.calls[0];
    expect(callArgs[0]).toBe(validEmail);
    expect(callArgs[1]).toBe(validPassword);
    expect(typeof callArgs[2]).toBe('function'); // setErrorMessage callback.
    expect(callArgs[3]).toBe(mockNavigate);
  });

  test('displays error message when registerRequest sets an error', async () => {
    // Simulate registerRequest calling the error setter with a specific error message.
    registerRequestMock.mockImplementation((email, password, setErrorMessage, navigate) => {
      setErrorMessage("app.registration.error.test");
    });

    render(<RegistrationForm />);

    // Query input fields by their name attributes.
    const emailInput = document.querySelector('input[name="email"]');
    const passwordInput = document.querySelector('input[name="password"]');
    const confirmPasswordInput = document.querySelector('input[name="confirmPassword"]');
    const submitButton = screen.getByRole('button', { name: /app.registr.form.reg.buttom/i });

    // Fill in valid data.
    fireEvent.change(emailInput, { target: { value: 'error@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'Abcdef1!' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'Abcdef1!' } });

    // Wait for Formik to update the form state.
    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });

    // Submit the form.
    fireEvent.click(submitButton);

    // Check that the error message (rendered via Message) is displayed.
    await waitFor(() => {
      expect(screen.getByText("app.registration.error.test")).toBeInTheDocument();
    });
  });
});
