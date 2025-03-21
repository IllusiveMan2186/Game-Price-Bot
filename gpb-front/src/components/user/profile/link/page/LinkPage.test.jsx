import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import LinkPage from './LinkPage';

// Mock Message to simply render the provided string.
jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <span>{string}</span>
}));

// Mock the useNavigation hook.
jest.mock('@contexts/NavigationContext', () => ({
  useNavigation: jest.fn(),
}));

// Mock the useLinkActions hook.
jest.mock('@hooks/user/useLinkActions', () => ({
  useLinkActions: jest.fn(),
}));

describe('LinkPage', () => {
  let accountLinkRequestMock;
  let mockNavigate;

  beforeEach(() => {
    accountLinkRequestMock = jest.fn();
    mockNavigate = jest.fn();

    const { useNavigation } = require('@contexts/NavigationContext');
    useNavigation.mockReturnValue(mockNavigate);

    const { useLinkActions } = require('@hooks/user/useLinkActions');
    useLinkActions.mockReturnValue({
      accountLinkRequest: accountLinkRequestMock,
    });
  });

  it('should renders initial state with title, token input and disabled submit button', () => {
    render(<LinkPage />);

    // Check that the page title is rendered.
    expect(screen.getByText('app.user.link.enter')).toBeInTheDocument();

    // The label for the token input is rendered via Message.
    expect(screen.getByText('app.user.link.enter.description')).toBeInTheDocument();

    // Get the token input by its associated label.
    const tokenInput = screen.getByLabelText('app.user.link.enter.description');
    expect(tokenInput).toBeInTheDocument();
    expect(tokenInput.value).toBe('');

    // The submit button should be disabled since the token is empty.
    const submitButton = screen.getByRole('button', { name: /app.user.link.form.button/i });
    expect(submitButton).toBeDisabled();
  });

  it('should shows validation error for an empty token', async () => {
    render(<LinkPage />);
    const tokenInput = screen.getByLabelText('app.user.link.enter.description');

    // Simulate a blur event on an empty input to trigger validation.
    fireEvent.blur(tokenInput);

    // Wait for Yup to validate and set the error.
    await waitFor(() => {
      expect(screen.getByText('app.user.link.token.error.required')).toBeInTheDocument();
    });
  });

  it('should enables submit button when a valid token is provided', async () => {
    render(<LinkPage />);
    const tokenInput = screen.getByLabelText('app.user.link.enter.description');
    const submitButton = screen.getByRole('button', { name: /app.user.link.form.button/i });

    // Enter a valid token.
    fireEvent.change(tokenInput, { target: { value: 'valid-token' } });
    fireEvent.blur(tokenInput);

    // Wait until the validation clears the error and the form becomes valid.
    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });
  });

  it('should submits the form and calls accountLinkRequest with correct arguments', async () => {
    render(<LinkPage />);
    const tokenInput = screen.getByLabelText('app.user.link.enter.description');
    const submitButton = screen.getByRole('button', { name: /app.user.link.form.button/i });

    // Enter a valid token.
    fireEvent.change(tokenInput, { target: { value: 'valid-token' } });
    fireEvent.blur(tokenInput);

    // Wait for the button to become enabled.
    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });

    // Submit the form.
    fireEvent.click(submitButton);

    // Verify that accountLinkRequest is called with:
    // - the token,
    // - a callback function (to set the error message),
    // - and the navigate function.
    await waitFor(() => {
      expect(accountLinkRequestMock).toHaveBeenCalledWith('valid-token', expect.any(Function), mockNavigate);
    });
  });

  it('should displays error message when accountLinkRequest sets an error', async () => {
    // Simulate accountLinkRequest calling the error setter.
    accountLinkRequestMock.mockImplementation((token, setErrorMessage, navigate) => {
      setErrorMessage('app.some.link.error');
    });

    render(<LinkPage />);
    const tokenInput = screen.getByLabelText('app.user.link.enter.description');
    const submitButton = screen.getByRole('button', { name: /app.user.link.form.button/i });

    // Provide a valid token.
    fireEvent.change(tokenInput, { target: { value: 'valid-token' } });
    fireEvent.blur(tokenInput);

    // Wait for the form to be valid.
    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });

    // Submit the form.
    fireEvent.click(submitButton);

    // Wait for the error message to appear.
    await waitFor(() => {
      expect(screen.getByText('app.some.link.error')).toBeInTheDocument();
    });
  });
});
