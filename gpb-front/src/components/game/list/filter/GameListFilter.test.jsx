import { render, screen, fireEvent } from '@testing-library/react';
import GameListFilter from './GameListFilter';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';

jest.mock('@util/message', () => ({
    __esModule: true,
    default: ({ string }) => <span>{string}</span>,
}));

jest.mock('@contexts/NavigationContext', () => ({
    useNavigation: () => jest.fn(),
}));
jest.mock('@util/navigationUtils', () => ({
    reloadPage: jest.fn(),
}));

const mockStore = configureStore([]);

let store;

describe('GameListFilter Component', () => {

    beforeEach(() => {
        store = mockStore({
            params: {
                genres: [],
                types: [],
                minPrice: 0,
                maxPrice: 10000,
            },
        });
    });

    const renderWithStore = () =>
        render(
            <Provider store={store}>
                <GameListFilter />
            </Provider>
        );

    it('should render filter titles correctly', () => {
        renderWithStore();

        expect(screen.getByText('app.game.filter.title')).toBeInTheDocument();
        expect(screen.getByText('app.game.filter.genre.title')).toBeInTheDocument();
        expect(screen.getByText('app.game.info.type')).toBeInTheDocument();
    });

    it('should handle price validation', () => {
        renderWithStore();

        const [minPriceInput, maxPriceInput] = screen.getAllByRole('spinbutton');

        fireEvent.change(minPriceInput, { target: { value: '50' } });
        fireEvent.change(maxPriceInput, { target: { value: '40' } });

        expect(screen.getByText('app.game.error.price')).toBeInTheDocument();

        fireEvent.change(maxPriceInput, { target: { value: '60' } });

        expect(screen.queryByText('app.game.error.price')).not.toBeInTheDocument();
    });

    it('should enable filter button when form is valid', () => {
        renderWithStore();

        const [minPriceInput, maxPriceInput] = screen.getAllByRole('spinbutton');
        const button = screen.getByRole('button', {
            name: /app.game.filter.accept.button/i,
        });

        expect(button).toBeDisabled();

        fireEvent.change(minPriceInput, { target: { value: '100' } });
        fireEvent.change(maxPriceInput, { target: { value: '200' } });

        expect(button).toBeEnabled();
    });
});
