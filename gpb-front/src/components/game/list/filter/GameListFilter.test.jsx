import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import GameListFilter from './GameListFilter';
import * as constants from '@util/constants';

jest.mock('@util/message', () => ({
    __esModule: true,
    default: ({ string }) => <span>{string}</span>,
}));

describe('GameListFilter Component', () => {
    const mockSearchParams = {
        get: jest.fn(),
        has: jest.fn(),
        append: jest.fn(),
        delete: jest.fn(),
    };
    const mockParameterSetOrRemove = jest.fn();
    const mockReloadPage = jest.fn();
    const mockSetPage = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
    });

    it('should renders filter titles correctly', () => {
        render(
            <GameListFilter
                searchParams={mockSearchParams}
                parameterSetOrRemove={mockParameterSetOrRemove}
                reloadPage={mockReloadPage}
                setPage={mockSetPage}
            />
        );

        expect(screen.getByText('app.game.filter.title')).toBeInTheDocument();
        expect(screen.getByText('app.game.filter.price.title')).toBeInTheDocument();
        expect(screen.getByText('app.game.filter.genre.title')).toBeInTheDocument();
        expect(screen.getByText('app.game.info.type')).toBeInTheDocument();
    });

    it('should handles price input changes and validation', () => {
        render(
            <GameListFilter
                searchParams={mockSearchParams}
                parameterSetOrRemove={mockParameterSetOrRemove}
                reloadPage={mockReloadPage}
                setPage={mockSetPage}
            />
        );

        const [minPriceInput, maxPriceInput] = screen.getAllByRole('spinbutton');

        fireEvent.change(minPriceInput, { target: { value: '50' } });
        fireEvent.change(maxPriceInput, { target: { value: '40' } });

        expect(screen.getByText('app.game.error.price')).toBeInTheDocument();

        fireEvent.change(maxPriceInput, { target: { value: '60' } });

        expect(screen.queryByText('app.game.error.price')).toBeNull();
    });

    it('should handles genre checkbox changes', () => {
        render(
            <GameListFilter
                searchParams={mockSearchParams}
                parameterSetOrRemove={mockParameterSetOrRemove}
                reloadPage={mockReloadPage}
                setPage={mockSetPage}
            />
        );

        const genreCheckbox = screen.getByRole('checkbox', {
            name: /action/i
        });

        fireEvent.click(genreCheckbox);

        expect(mockSearchParams.append).toHaveBeenCalledWith('genre', constants.ganresOptions[0].value.toUpperCase());
        expect(mockSetPage).toHaveBeenCalledWith(1);
    });

    it('should enables filter button when form is valid and changed', () => {
        render(
            <GameListFilter
                searchParams={mockSearchParams}
                parameterSetOrRemove={mockParameterSetOrRemove}
                reloadPage={mockReloadPage}
                setPage={mockSetPage}
            />
        );

        const [minPriceInput, maxPriceInput] = screen.getAllByRole('spinbutton');
        const filterButton = screen.getByRole('button', {
            name: /app.game.filter.accept.button/i
        });

        expect(filterButton).toBeDisabled();

        fireEvent.change(minPriceInput, { target: { value: '50' } });
        fireEvent.change(maxPriceInput, { target: { value: '100' } });

        expect(filterButton).toBeEnabled();
    });

    it('should calls parameterSetOrRemove and reloadPage on filter button click', () => {
        render(
            <GameListFilter
                searchParams={mockSearchParams}
                parameterSetOrRemove={mockParameterSetOrRemove}
                reloadPage={mockReloadPage}
                setPage={mockSetPage}
            />
        );

        const [minPriceInput, maxPriceInput] = screen.getAllByRole('spinbutton');
        const filterButton = screen.getByText('app.game.filter.accept.button');

        fireEvent.change(minPriceInput, { target: { value: '50' } });
        fireEvent.change(maxPriceInput, { target: { value: '100' } });

        fireEvent.click(filterButton);

        expect(mockParameterSetOrRemove).toHaveBeenCalledWith('minPrice', 50, 0);
        expect(mockParameterSetOrRemove).toHaveBeenCalledWith('maxPrice', 100, 10000);
        expect(mockReloadPage).toHaveBeenCalled();
    });
});
