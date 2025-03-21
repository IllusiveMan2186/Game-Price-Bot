import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import EmailChange from './EmailChange';

// Mock the Message component so it simply renders the provided string.
jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <span>{string}</span>
}));

// Mock the useNavigation hook.
jest.mock('@contexts/NavigationContext', () => ({
  useNavigation: jest.fn(),
}));

// Mock the useEmailActions hook.
jest.mock('@hooks/user/useEmailActions', () => ({
  useEmailActions: jest.fn(),
}));

describe('EmailChange', () => {
  let emailChangeRequestMock;
  let mockNavigate;

  beforeEach(() => {
    // Create fresh mocks for each test.
    emailChangeRequestMock = jest.fn();
    mockNavigate = jest.fn();

    const { useNavigation } = require('@contexts/NavigationContext');
    useNavigation.mockReturnValue(mockNavigate);

    const { useEmailActions } = require('@hooks/user/useEmailActions');
    useEmailActions.mockReturnValue({
      emailChangeRequest: emailChangeRequestMock,
    });
  });

  test('renders component with title, email input and disabled submit button initially', () => {
    render(<EmailChange />);

    // Verify the title is rendered.
    expect(screen.getByText('app.user.change.email')).toBeInTheDocument();

    // Get the email input using its associated label.
    const emailInput = screen.getByLabelText(/app.login.form.email/i);
    expect(emailInput).toBeInTheDocument();
    expect(emailInput.value).toBe('');

    // The submit button should be disabled initially (because email is empty).
    const submitButton = screen.getByRole('button', { name: /app.registr.form.reg.buttom/i });
    expect(submitButton).toBeDisabled();

    // Initially, no validation or submission error messages should be present.
    expect(screen.queryByText(/app.login.form.error/)).toBeNull();
    expect(screen.queryByText(/app.some.error/)).toBeNull();
  });

  test('displays validation error for an invalid email', async () => {
    render(<EmailChange />);
    const emailInput = screen.getByLabelText(/app.login.form.email/i);

    // Enter an invalid email.
    fireEvent.change(emailInput, { target: { value: 'invalid' } });
    fireEvent.blur(emailInput);

    // Wait for the validation error to appear.
    await waitFor(() => {
      // Expect an error message indicating a wrong email.
      expect(screen.getByText(/app.login.form.error.wrong.email/i)).toBeInTheDocument();
    });
  });

  test('enables submit button for valid email and calls emailChangeRequest on form submission', async () => {
    render(<EmailChange />);
    const emailInput = screen.getByLabelText(/app.login.form.email/i);
    const submitButton = screen.getByRole('button', { name: /app.registr.form.reg.buttom/i });

    // Enter a valid email.
    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.blur(emailInput);

    // Wait for the validation to pass so that the submit button becomes enabled.
    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });

    // Submit the form.
    fireEvent.click(submitButton);

    // Verify that emailChangeRequest is called with the valid email and a callback function.
    await waitFor(() => {
      expect(emailChangeRequestMock).toHaveBeenCalledWith('test@example.com', expect.any(Function));
    });
  });

  test('displays error message when emailChangeRequest sets an error', async () => {
    // Simulate emailChangeRequest calling the error setter with a specific error.
    emailChangeRequestMock.mockImplementation((email, setErrorMessage) => {
      setErrorMessage('app.some.error');
    });

    render(<EmailChange />);
    const emailInput = screen.getByLabelText(/app.login.form.email/i);
    const submitButton = screen.getByRole('button', { name: /app.registr.form.reg.buttom/i });

    // Enter a valid email.
    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.blur(emailInput);

    // Wait for the form to be valid.
    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });

    // Submit the form.
    fireEvent.click(submitButton);

    // Check that the error message is rendered.
    await waitFor(() => {
      expect(screen.getByText('app.some.error')).toBeInTheDocument();
    });
  });
});
