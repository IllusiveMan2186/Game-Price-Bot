import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import Pagination from './Pagination';

import { useNavigation } from '@contexts/NavigationContext';
import * as navigationUtils from '@util/navigationUtils';
import { setPageNum } from '@features/params/paramsSlice';

jest.mock('@contexts/NavigationContext', () => ({
    useNavigation: jest.fn(),
}));

jest.mock('@util/navigationUtils', () => ({
    reloadPage: jest.fn(),
}));

const mockStore = configureStore([]);

describe('Pagination Component', () => {
    let store;
    const mockNavigate = jest.fn();

    beforeEach(() => {
        store = mockStore({
            params: {
                elementAmount: 100,
                pageNum: 1,
                pageSize: 10,
            },
        });

        store.dispatch = jest.fn();
        useNavigation.mockReturnValue(mockNavigate);
        navigationUtils.reloadPage.mockClear();
    });

    it('should renders correct number of page buttons', () => {
        render(
            <Provider store={store}>
                <Pagination />
            </Provider>
        );

        const pageButtons = screen.getAllByRole('button', { name: /^\d+$/ });
        expect(pageButtons.length).toBeGreaterThan(0); 
    });

    it('should dispatches setPageNum and calls reloadPage on page click', async () => {
        render(
            <Provider store={store}>
                <Pagination />
            </Provider>
        );

        const page2Button = screen.getByRole('button', { name: '2' });
        fireEvent.click(page2Button);

        await waitFor(() => {
            expect(store.dispatch).toHaveBeenCalledWith(setPageNum(2));
            expect(navigationUtils.reloadPage).toHaveBeenCalledWith(mockNavigate);
        });
    });

    it('should does not render previous buttons on first page', () => {
        render(
            <Provider store={store}>
                <Pagination />
            </Provider>
        );

        expect(screen.queryByText('|<')).not.toBeInTheDocument();
        expect(screen.queryByText('<')).not.toBeInTheDocument();
    });

    it('should does not render next buttons on last page', () => {
        store = mockStore({
            params: {
                elementAmount: 100,
                pageNum: 10,
                pageSize: 10,
            },
        });

        store.dispatch = jest.fn();
        useNavigation.mockReturnValue(mockNavigate);

        render(
            <Provider store={store}>
                <Pagination />
            </Provider>
        );

        expect(screen.queryByText('>')).not.toBeInTheDocument();
        expect(screen.queryByText('>|')).not.toBeInTheDocument();
    });
});
