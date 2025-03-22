import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import GameStoresList from './GameStoresList';
import { useAuth } from '@contexts/AuthContext';
import { useGameStoreActions } from '@hooks/game/useGameStoreActions';

jest.mock('@contexts/AuthContext', () => ({
  useAuth: jest.fn(),
}));

jest.mock('@hooks/game/useGameStoreActions', () => ({
  useGameStoreActions: jest.fn(),
}));

describe('GameStoresList Component', () => {
  const mockRemoveGameInStoreRequest = jest.fn();
  const mockNavigate = jest.fn();

  const sampleStores = [
    {
      id: '1',
      url: 'https://store1.com/game',
      clientType: 'Steam',
      available: true,
      price: '$10.00',
      discount: 20,
      discountPrice: '$8.00',
    },
    {
      id: '2',
      url: 'https://store2.com/game',
      clientType: 'Epic',
      available: false,
      price: '$15.00',
      discount: 0,
      discountPrice: '$15.00',
    },
  ];

  beforeEach(() => {
    jest.clearAllMocks();
    useAuth.mockReturnValue({
      isUserAdmin: jest.fn().mockReturnValue(true),
    });
    useGameStoreActions.mockReturnValue({
      removeGameInStoreRequest: mockRemoveGameInStoreRequest,
    });
  });

  it('should render a list of game stores', () => {
    render(<GameStoresList stores={sampleStores} navigate={mockNavigate} />);
    expect(screen.getByText('store1.com')).toBeInTheDocument();
    expect(screen.getByText('store2.com')).toBeInTheDocument();
  });

  it('should display remove buttons for admin users', () => {
    render(<GameStoresList stores={sampleStores} navigate={mockNavigate} />);
    const removeButtons = screen.getAllByRole('button', { name: /Remove game/i });
    expect(removeButtons).toHaveLength(sampleStores.length);
  });

  it('should call removeGameInStoreRequest and navigate on store removal', () => {
    render(<GameStoresList stores={sampleStores} navigate={mockNavigate} />);
    const removeButton = screen.getAllByRole('button', { name: /Remove game/i })[0];
    fireEvent.click(removeButton);
    expect(mockRemoveGameInStoreRequest).toHaveBeenCalledWith('1', expect.any(Function));
  });
});
