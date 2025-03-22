import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import AuthViewSwitcher from './AuthViewSwitcher';

jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <span>{string}</span>
}));

jest.mock('@components/user/auth/login/LoginForm', () => () => <div data-testid="login-form" />);
jest.mock('@components/user/auth/registration/RegistrationForm', () => () => <div data-testid="registration-form" />);

describe('AuthViewSwitcher', () => {
  it('should renders login view by default', () => {
    render(<AuthViewSwitcher />);
    
    const loginButton = screen.getByRole('button', { name: /app.login/i });
    expect(loginButton).toHaveClass('active');

    const registerButton = screen.getByRole('button', { name: /app.registr/i });
    expect(registerButton).not.toHaveClass('active');

    const loginContent = document.getElementById('pills-login');
    expect(loginContent.className).toMatch(/show active/);

    const registerContent = document.getElementById('pills-register');
    expect(registerContent.className).not.toMatch(/show active/);

    expect(screen.getByTestId('login-form')).toBeInTheDocument();
  });

  it('should switches to registration view when register tab is clicked', () => {
    render(<AuthViewSwitcher />);
    
    const registerButton = screen.getByRole('button', { name: /app.registr/i });
    fireEvent.click(registerButton);

    expect(registerButton).toHaveClass('active');

    const loginButton = screen.getByRole('button', { name: /app.login/i });
    expect(loginButton).not.toHaveClass('active');

    const registerContent = document.getElementById('pills-register');
    expect(registerContent.className).toMatch(/show active/);

    const loginContent = document.getElementById('pills-login');
    expect(loginContent.className).not.toMatch(/show active/);

    expect(screen.getByTestId('registration-form')).toBeInTheDocument();
  });

  it('should switches back to login view when login tab is clicked after switching to register', () => {
    render(<AuthViewSwitcher />);
    
    const registerButton = screen.getByRole('button', { name: /app.registr/i });
    fireEvent.click(registerButton);
    
    const loginButton = screen.getByRole('button', { name: /app.login/i });
    fireEvent.click(loginButton);

    expect(loginButton).toHaveClass('active');
    expect(registerButton).not.toHaveClass('active');

    const loginContent = document.getElementById('pills-login');
    expect(loginContent.className).toMatch(/show active/);

    const registerContent = document.getElementById('pills-register');
    expect(registerContent.className).not.toMatch(/show active/);

    expect(screen.getByTestId('login-form')).toBeInTheDocument();
  });
});
