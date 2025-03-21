import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import AuthViewSwitcher from './AuthViewSwitcher';

// Mock Message so that it simply renders the provided string.
jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <span>{string}</span>
}));

// Mock LoginForm and RegistrationForm components.
jest.mock('@components/user/auth/login/LoginForm', () => () => <div data-testid="login-form" />);
jest.mock('@components/user/auth/registration/RegistrationForm', () => () => <div data-testid="registration-form" />);

describe('AuthViewSwitcher', () => {
  test('renders login view by default', () => {
    render(<AuthViewSwitcher />);
    
    // By default, the login tab should have the "active" class.
    const loginButton = screen.getByRole('button', { name: /app.login/i });
    expect(loginButton).toHaveClass('active');

    // The register tab should not be active.
    const registerButton = screen.getByRole('button', { name: /app.registr/i });
    expect(registerButton).not.toHaveClass('active');

    // The login content should be active.
    const loginContent = document.getElementById('pills-login');
    expect(loginContent.className).toMatch(/show active/);

    // The registration content should not be active.
    const registerContent = document.getElementById('pills-register');
    expect(registerContent.className).not.toMatch(/show active/);

    // Check that LoginForm is rendered.
    expect(screen.getByTestId('login-form')).toBeInTheDocument();
  });

  test('switches to registration view when register tab is clicked', () => {
    render(<AuthViewSwitcher />);
    
    // Click on the registration tab button.
    const registerButton = screen.getByRole('button', { name: /app.registr/i });
    fireEvent.click(registerButton);

    // Registration button should now have the active class.
    expect(registerButton).toHaveClass('active');

    // The login tab should not be active.
    const loginButton = screen.getByRole('button', { name: /app.login/i });
    expect(loginButton).not.toHaveClass('active');

    // Registration content should be active.
    const registerContent = document.getElementById('pills-register');
    expect(registerContent.className).toMatch(/show active/);

    // Login content should not be active.
    const loginContent = document.getElementById('pills-login');
    expect(loginContent.className).not.toMatch(/show active/);

    // Verify that the RegistrationForm is rendered.
    expect(screen.getByTestId('registration-form')).toBeInTheDocument();
  });

  test('switches back to login view when login tab is clicked after switching to register', () => {
    render(<AuthViewSwitcher />);
    
    // First, switch to the registration view.
    const registerButton = screen.getByRole('button', { name: /app.registr/i });
    fireEvent.click(registerButton);
    
    // Now, click on the login tab.
    const loginButton = screen.getByRole('button', { name: /app.login/i });
    fireEvent.click(loginButton);

    // Login button should be active.
    expect(loginButton).toHaveClass('active');
    // Registration button should not be active.
    expect(registerButton).not.toHaveClass('active');

    // Login content should be active.
    const loginContent = document.getElementById('pills-login');
    expect(loginContent.className).toMatch(/show active/);

    // Registration content should not be active.
    const registerContent = document.getElementById('pills-register');
    expect(registerContent.className).not.toMatch(/show active/);

    // Verify that the LoginForm is rendered.
    expect(screen.getByTestId('login-form')).toBeInTheDocument();
  });
});
