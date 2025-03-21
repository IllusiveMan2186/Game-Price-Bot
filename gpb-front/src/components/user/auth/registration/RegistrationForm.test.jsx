import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import RegistrationForm from './RegistrationForm';

jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <span>{string}</span>
}));

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
    registerRequestMock = jest.fn();
    mockNavigate = jest.fn();

    const { useAuthActions } = require('@hooks/user/useAuthActions');
    useAuthActions.mockReturnValue({
      registerRequest: registerRequestMock,
    });

    const { useNavigation } = require('@contexts/NavigationContext');
    useNavigation.mockReturnValue(mockNavigate);
  });

  it('should submits valid data calling registerRequest', async () => {
    render(<RegistrationForm />);

    const emailInput = document.querySelector('input[name="email"]');
    const passwordInput = document.querySelector('input[name="password"]');
    const confirmPasswordInput = document.querySelector('input[name="confirmPassword"]');
    const submitButton = screen.getByRole('button', { name: /app.registr.form.reg.buttom/i });

    const validEmail = 'test@example.com';
    const validPassword = 'Abcdef1!';
    fireEvent.change(emailInput, { target: { value: validEmail } });
    fireEvent.change(passwordInput, { target: { value: validPassword } });
    fireEvent.change(confirmPasswordInput, { target: { value: validPassword } });

    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });

    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(registerRequestMock).toHaveBeenCalled();
    });

    const callArgs = registerRequestMock.mock.calls[0];
    expect(callArgs[0]).toBe(validEmail);
    expect(callArgs[1]).toBe(validPassword);
    expect(typeof callArgs[2]).toBe('function');
    expect(callArgs[3]).toBe(mockNavigate);
  });

  it('should displays error message when registerRequest sets an error', async () => {
    registerRequestMock.mockImplementation((email, password, setErrorMessage, navigate) => {
      setErrorMessage("app.registration.error.test");
    });

    render(<RegistrationForm />);

    const emailInput = document.querySelector('input[name="email"]');
    const passwordInput = document.querySelector('input[name="password"]');
    const confirmPasswordInput = document.querySelector('input[name="confirmPassword"]');
    const submitButton = screen.getByRole('button', { name: /app.registr.form.reg.buttom/i });

    fireEvent.change(emailInput, { target: { value: 'error@example.com' } });
    fireEvent.change(passwordInput, { target: { value: 'Abcdef1!' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'Abcdef1!' } });

    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });

    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText("app.registration.error.test")).toBeInTheDocument();
    });
  });
});
