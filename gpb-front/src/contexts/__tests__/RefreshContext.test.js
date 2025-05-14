const mockSetAccessToken = jest.fn();
const mockGetAccessToken = jest.fn(() => 'mock-access');
const mockGetLinkToken = jest.fn(() => 'mock-link');
const mockLogout = jest.fn();
const mockNavigate = jest.fn();
const mockRegisterAuthHandlers = jest.fn();

jest.mock('@contexts/AuthContext', () => ({
  useAuth: () => ({
    setAccessToken: mockSetAccessToken,
    getAccessToken: mockGetAccessToken,
    getLinkToken: mockGetLinkToken,
    logout: mockLogout,
  }),
}));

jest.mock('@contexts/NavigationContext', () => ({
  useNavigation: () => mockNavigate,
}));

jest.mock('@services/httpService', () => ({
  registerAuthHandlers: (...args) => mockRegisterAuthHandlers(...args),
}));

import { render, waitFor } from '@testing-library/react';
import { RefreshProvider } from '../RefreshContext';

describe('RefreshProvider', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call registerAuthHandlers on mount', async () => {
    render(
      <RefreshProvider>
        <div>child content</div>
      </RefreshProvider>
    );

    await waitFor(() => {
      expect(mockRegisterAuthHandlers).toHaveBeenCalledWith({
        getAccessToken: mockGetAccessToken,
        setAccessToken: mockSetAccessToken,
        getLinkToken: mockGetLinkToken,
        logout: mockLogout,
        navigate: mockNavigate,
      });
    });
  });

  it('should render children', () => {
    const { getByText } = render(
      <RefreshProvider>
        <div>child content</div>
      </RefreshProvider>
    );
    expect(getByText('child content')).toBeInTheDocument();
  });
});
