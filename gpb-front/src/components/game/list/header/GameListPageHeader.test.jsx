import React from 'react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import selectEvent from 'react-select-event';
import GameListPageHeader from './GameListPageHeader';
import * as gameActions from '@hooks/game/useGameActions';
import { useNavigation } from '@contexts/NavigationContext';
import { NotificationManager } from 'react-notifications';
import Message from '@util/message';


jest.mock('@hooks/game/useGameActions');
jest.mock('@contexts/NavigationContext');
jest.mock('react-notifications');

jest.mock('@util/message', () => ({
    __esModule: true,
    default: ({ string }) => <span>{string}</span>,
}));


describe('GameListPageHeader Component', () => {
    const mockNavigate = jest.fn();
    const mockGetGameByUrlRequest = jest.fn();

    beforeEach(() => {
        // Reset mocks before each test
        jest.clearAllMocks();

        // Mock implementations
        useNavigation.mockReturnValue(mockNavigate);
        gameActions.useGameActions.mockReturnValue({
            getGameByUrlRequest: mockGetGameByUrlRequest,
        });
    });

    it('should render search input and buttons', () => {
        render(<GameListPageHeader searchParams={new URLSearchParams()} updateSearchParams={jest.fn()} />);

        expect(screen.getByPlaceholderText('app.game.filter.search.title')).toBeInTheDocument();
        expect(screen.getByText('app.game.filter.search.button')).toBeInTheDocument();
    });

    it('should handle search input change', () => {
        render(<GameListPageHeader searchParams={new URLSearchParams()} updateSearchParams={jest.fn()} />);

        const searchInput = screen.getByPlaceholderText('app.game.filter.search.title');
        userEvent.type(searchInput, 'Test Game');

        expect(searchInput).toHaveValue('Test Game');
    });

    it('should display error notification for empty search', () => {
        render(<GameListPageHeader searchParams={new URLSearchParams()} updateSearchParams={jest.fn()} />);

        const searchButton = screen.getByText('app.game.filter.search.button');
        userEvent.click(searchButton);

        expect(NotificationManager.error).toHaveBeenCalledWith(
            <Message string={'app.game.error.name.empty'} />,
            <Message string={'app.game.error.title'} />
        );
    });

    it('should call getGameByUrlRequest for valid URL search', () => {
        render(<GameListPageHeader searchParams={new URLSearchParams()} updateSearchParams={jest.fn()} />);

        const searchInput = screen.getByPlaceholderText('app.game.filter.search.title');
        userEvent.type(searchInput, 'https://valid-game-url.com');
        const searchButton = screen.getByText('app.game.filter.search.button');
        userEvent.click(searchButton);

        expect(mockGetGameByUrlRequest).toHaveBeenCalledWith('https://valid-game-url.com', mockNavigate);
    });

    it('should navigate to search page for valid name search', () => {
        render(<GameListPageHeader searchParams={new URLSearchParams()} updateSearchParams={jest.fn()} />);

        const searchInput = screen.getByPlaceholderText('app.game.filter.search.title');
        userEvent.type(searchInput, 'Valid Game Name');
        const searchButton = screen.getByText('app.game.filter.search.button');
        userEvent.click(searchButton);

        expect(mockNavigate).toHaveBeenCalledWith('/search/Valid Game Name/');
    });

    it('should update sortBy parameter and reload page on sort change', async () => {
        const mockUpdateSearchParams = jest.fn();
        const mockReloadPage = jest.fn();

        render(
            <GameListPageHeader
                searchParams={new URLSearchParams()}
                updateSearchParams={mockUpdateSearchParams}
                reloadPage={mockReloadPage}
            />
        );

        // Find input of sort
        const sortInput = screen.getByLabelText('Sort By');
        await userEvent.click(sortInput);

        // Find option
        const option = await screen.findByText('app.game.filter.sort.name.reverse');
        await userEvent.click(option);


        // 5. Check calls
        expect(mockUpdateSearchParams).toHaveBeenCalledWith('sortBy', 'name-DESC', 'name-ASC');
        expect(mockReloadPage).toHaveBeenCalled();
    });

    it('should update pageSize parameter and reload page on page size change', async () => {
        const mockUpdateSearchParams = jest.fn();
        const mockReloadPage = jest.fn();
        render(
            <GameListPageHeader
                searchParams={new URLSearchParams()}
                updateSearchParams={mockUpdateSearchParams}
                reloadPage={mockReloadPage}
            />
        );

        const pageSizeSelect = screen.getByLabelText('Page Size');
        await selectEvent.select(pageSizeSelect, '50');

        expect(mockUpdateSearchParams).toHaveBeenCalledWith('pageSize', '50', '25');
        expect(mockUpdateSearchParams).toHaveBeenCalledWith('pageNum', 1, 1);
        expect(mockReloadPage).toHaveBeenCalled();
    });
});
