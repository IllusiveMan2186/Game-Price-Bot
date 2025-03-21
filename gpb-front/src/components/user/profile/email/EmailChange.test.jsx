import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import EmailChange from './EmailChange';

jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <span>{string}</span>
}));

jest.mock('@contexts/NavigationContext', () => ({
  useNavigation: jest.fn(),
}));

jest.mock('@hooks/user/useEmailActions', () => ({
  useEmailActions: jest.fn(),
}));

describe('EmailChange', () => {
  let emailChangeRequestMock;
  let mockNavigate;

  beforeEach(() => {
    emailChangeRequestMock = jest.fn();
    mockNavigate = jest.fn();

    const { useNavigation } = require('@contexts/NavigationContext');
    useNavigation.mockReturnValue(mockNavigate);

    const { useEmailActions } = require('@hooks/user/useEmailActions');
    useEmailActions.mockReturnValue({
      emailChangeRequest: emailChangeRequestMock,
    });
  });

  it('should renders component with title, email input and disabled submit button initially', () => {
    render(<EmailChange />);

    expect(screen.getByText('app.user.change.email')).toBeInTheDocument();

    const emailInput = screen.getByLabelText(/app.login.form.email/i);
    expect(emailInput).toBeInTheDocument();
    expect(emailInput.value).toBe('');

    const submitButton = screen.getByRole('button', { name: /app.registr.form.reg.buttom/i });
    expect(submitButton).toBeDisabled();

    expect(screen.queryByText(/app.login.form.error/)).toBeNull();
    expect(screen.queryByText(/app.some.error/)).toBeNull();
  });

  it('should displays validation error for an invalid email', async () => {
    render(<EmailChange />);
    const emailInput = screen.getByLabelText(/app.login.form.email/i);

    fireEvent.change(emailInput, { target: { value: 'invalid' } });
    fireEvent.blur(emailInput);

    await waitFor(() => {
      expect(screen.getByText(/app.login.form.error.wrong.email/i)).toBeInTheDocument();
    });
  });

  it('should enables submit button for valid email and calls emailChangeRequest on form submission', async () => {
    render(<EmailChange />);
    const emailInput = screen.getByLabelText(/app.login.form.email/i);
    const submitButton = screen.getByRole('button', { name: /app.registr.form.reg.buttom/i });

    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.blur(emailInput);

    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });

    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(emailChangeRequestMock).toHaveBeenCalledWith('test@example.com', expect.any(Function));
    });
  });

  it('should displays error message when emailChangeRequest sets an error', async () => {
    emailChangeRequestMock.mockImplementation((email, setErrorMessage) => {
      setErrorMessage('app.some.error');
    });

    render(<EmailChange />);
    const emailInput = screen.getByLabelText(/app.login.form.email/i);
    const submitButton = screen.getByRole('button', { name: /app.registr.form.reg.buttom/i });

    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.blur(emailInput);

    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });

    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('app.some.error')).toBeInTheDocument();
    });
  });
});
