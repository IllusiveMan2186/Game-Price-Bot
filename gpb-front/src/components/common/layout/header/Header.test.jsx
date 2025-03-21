// Header.test.jsx
import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import Header from './Header';

jest.mock('@contexts/NavigationContext', () => ({
  useNavigation: jest.fn(),
}));

jest.mock('@contexts/AuthContext', () => ({
  useAuth: () => ({
    logout: jest.fn(),
  }),
}));

jest.mock('@hooks/user/useAuthActions', () => ({
  useAuthActions: () => ({
    userLogoutRequest: jest.fn(),
  }),
}));

jest.mock('@components/common/button/Buttons', () => ({ logout }) => (
  <button onClick={logout}>Logout</button>
));

describe('Header', () => {
  const mockNavigate = jest.fn();
  const mockLogout = jest.fn();
  const mockUserLogoutRequest = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();

    require('@contexts/NavigationContext').useNavigation.mockReturnValue(mockNavigate);
    require('@contexts/AuthContext').useAuth = () => ({
      logout: mockLogout,
    });
    require('@hooks/user/useAuthActions').useAuthActions = () => ({
      userLogoutRequest: mockUserLogoutRequest,
    });
  });

  it('renders logo and title', () => {
    render(<Header />);
    expect(screen.getByAltText('logo')).toBeInTheDocument();
    expect(screen.getByText('GPB')).toBeInTheDocument();
  });

  it('navigates to home and reloads on title click', () => {
    render(<Header />);
    fireEvent.click(screen.getByText('GPB'));

    expect(mockNavigate).toHaveBeenCalledWith('/');
    expect(mockNavigate).toHaveBeenCalledWith(0);
  });

  it('calls logout and userLogoutRequest on logout button click', async () => {
    render(<Header />);
    fireEvent.click(screen.getByText('Logout'));

    expect(mockLogout).toHaveBeenCalledWith(mockNavigate);
    expect(mockUserLogoutRequest).toHaveBeenCalled();
  });
});
