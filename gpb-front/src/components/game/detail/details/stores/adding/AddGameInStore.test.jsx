import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import AddGameInStore from './AddGameInStore';
import { useTranslation } from 'react-i18next';
import { useGameStoreActions } from '@hooks/game/useGameStoreActions';

jest.mock('react-i18next', () => ({
  useTranslation: jest.fn(),
}));

jest.mock('@hooks/game/useGameStoreActions', () => ({
  useGameStoreActions: jest.fn(),
}));

jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <span>{string}</span>,
}));

describe('AddGameInStore Component', () => {
  const mockAddGameInStoreByUrlRequest = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    useTranslation.mockReturnValue({ t: (key) => key });
    useGameStoreActions.mockReturnValue({
      addGameInStoreByUrlRequest: mockAddGameInStoreByUrlRequest,
    });
  });

  it('should display the add icon initially', () => {
    render(<AddGameInStore gameId="123" />);
    expect(screen.getByAltText('Add Game in Store')).toBeInTheDocument();
  });

  it('should open the form when the add icon is clicked', () => {
    render(<AddGameInStore gameId="123" />);
    fireEvent.click(screen.getByAltText('Add Game in Store'));
    expect(screen.getByPlaceholderText('app.game.add.placeholder')).toBeInTheDocument();
  });

  it('should call addGameInStoreByUrlRequest with correct URL and gameId', async () => {
    render(<AddGameInStore gameId="123" />);
    fireEvent.click(screen.getByAltText('Add Game in Store'));
    const input = screen.getByPlaceholderText('app.game.add.placeholder');
    fireEvent.change(input, { target: { value: 'http://example.com/game' } });
    fireEvent.click(screen.getByText('app.game.add.button'));

    await waitFor(() => {
      expect(mockAddGameInStoreByUrlRequest).toHaveBeenCalledWith('123', 'http://example.com/game');
    });
  });

  it('should display confirmation message after successful submission', async () => {
    mockAddGameInStoreByUrlRequest.mockResolvedValueOnce();
    render(<AddGameInStore gameId="123" />);
    fireEvent.click(screen.getByAltText('Add Game in Store'));
    const input = screen.getByPlaceholderText('app.game.add.placeholder');
    fireEvent.change(input, { target: { value: 'http://example.com/game' } });
    fireEvent.click(screen.getByText('app.game.add.button'));

    await waitFor(() => {
      expect(screen.getByText('app.game.add.send.message')).toBeInTheDocument();
    });
  });
});
