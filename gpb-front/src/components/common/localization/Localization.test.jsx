import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import Localization from './Localization';
import { useAuth } from '@contexts/AuthContext';
import { useUserActions } from '@hooks/user/useUserActions';
import { changeLanguage } from 'i18next';
import { getLocale, setLocale } from '@util/userDataUtils';

// Mock dependencies
jest.mock('@contexts/AuthContext', () => ({
  useAuth: jest.fn(),
}));

jest.mock('@hooks/user/useUserActions', () => ({
  useUserActions: jest.fn(),
}));

jest.mock('i18next', () => ({
  changeLanguage: jest.fn(),
}));

jest.mock('@util/userDataUtils', () => ({
  getLocale: jest.fn(),
  setLocale: jest.fn(),
}));

jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <span>{string}</span>,
}));

describe('Localization Component', () => {
  const mockLocaleChangeRequest = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    useAuth.mockReturnValue({ isUserAuth: true });
    useUserActions.mockReturnValue({
      localeChangeRequest: mockLocaleChangeRequest,
    });
    getLocale.mockReturnValue('en');
  });

  it('should render without errors', () => {
    render(<Localization />);
    expect(screen.getByText('app.footer.language')).toBeInTheDocument();
  });

  it('should display language buttons with correct labels', () => {
    render(<Localization />);
    expect(screen.getByText('RU')).toBeInTheDocument();
    expect(screen.getByText('UA')).toBeInTheDocument();
    expect(screen.getByText('EN')).toBeInTheDocument();
  });

  it('should highlight the button corresponding to the current locale', () => {
    getLocale.mockReturnValue('ua');
    render(<Localization />);
    const uaButton = screen.getByText('UA');
    expect(uaButton).toHaveStyle('backgroundColor: #0082ca');
  });

  it('should update locale and call related functions on button click', () => {
    render(<Localization />);
    const ruButton = screen.getByText('RU');
    fireEvent.click(ruButton);
    expect(setLocale).toHaveBeenCalledWith('ru');
    expect(changeLanguage).toHaveBeenCalledWith('ru');
    expect(mockLocaleChangeRequest).toHaveBeenCalledWith('ru');
  });

  it('should not call localeChangeRequest if user is not authenticated', () => {
    useAuth.mockReturnValue({ isUserAuth: false });
    render(<Localization />);
    const ruButton = screen.getByText('RU');
    fireEvent.click(ruButton);
    expect(setLocale).toHaveBeenCalledWith('ru');
    expect(changeLanguage).toHaveBeenCalledWith('ru');
    expect(mockLocaleChangeRequest).not.toHaveBeenCalled();
  });
});
