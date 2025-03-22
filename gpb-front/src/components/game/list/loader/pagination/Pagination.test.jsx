import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import Pagination from './Pagination';

describe('Pagination Component', () => {
    const mockReloadPage = jest.fn();
    const mockUpdateSearchParams = jest.fn();

    beforeEach(() => {
        mockReloadPage.mockClear();
        mockUpdateSearchParams.mockClear();
    });

    it('should renders correct number of page buttons', () => {
        render(
            <Pagination
                elementAmount={100}
                pageSize={10}
                page={1}
                reloadPage={mockReloadPage}
                updateSearchParams={mockUpdateSearchParams}
            />
        );

        const pageButtons = screen.getAllByRole('button', { name: /^\d+$/ });
        expect(pageButtons).toHaveLength(3);
    });

    it('should calls updateSearchParams and reloadPage on page button click', async () => {
        render(
            <Pagination
                elementAmount={100}
                pageSize={10}
                page={1}
                reloadPage={mockReloadPage}
                updateSearchParams={mockUpdateSearchParams}
            />
        );

        const page2Button = screen.getByRole('button', { name: '2' });
        fireEvent.click(page2Button);

        await waitFor(() => {
            expect(mockUpdateSearchParams).toHaveBeenCalledWith('pageNum', 2, 1);
            expect(mockReloadPage).toHaveBeenCalled();
        });
    });

    it('should disables previous buttons on first page', () => {
        render(
            <Pagination
                elementAmount={100}
                pageSize={10}
                page={1}
                reloadPage={mockReloadPage}
                updateSearchParams={mockUpdateSearchParams}
            />
        );

        expect(screen.queryByText('|<')).not.toBeInTheDocument();
        expect(screen.queryByText('<')).not.toBeInTheDocument();

        expect(mockUpdateSearchParams).not.toHaveBeenCalled();
        expect(mockReloadPage).not.toHaveBeenCalled();
    });

    it('should disables next buttons on last page', () => {
        render(
            <Pagination
                elementAmount={100}
                pageSize={10}
                page={10}
                reloadPage={mockReloadPage}
                updateSearchParams={mockUpdateSearchParams}
            />
        );

        expect(screen.queryByText('>')).not.toBeInTheDocument();
        expect(screen.queryByText('>|')).not.toBeInTheDocument();
    });
});
