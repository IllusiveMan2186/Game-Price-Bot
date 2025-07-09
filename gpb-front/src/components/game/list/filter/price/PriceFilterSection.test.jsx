// src/components/game/list/filter/price/PriceFilterSection.test.jsx
import React from 'react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

// Mock Message to just render its `string` prop
jest.mock('@util/message', () => ({ string }) => <>{string}</>);

import PriceFilterSection from './PriceFilterSection';

describe('PriceFilterSection', () => {
    const defaultProps = {
        minPrice: 0,
        maxPrice: 1000,
        onChange: jest.fn(),
        error: '',
    };

    beforeEach(() => {
        defaultProps.onChange.mockClear();
    });

    it('should renders the title via Message', () => {
        render(<PriceFilterSection {...defaultProps} />);
        expect(screen.getByText('app.game.filter.price.title')).toBeInTheDocument();
    });

    it('should renders min and max inputs with correct initial values', () => {
        render(<PriceFilterSection minPrice={50} maxPrice={500} onChange={() => { }} error="" />);

        const inputs = screen.getAllByRole('spinbutton');
        const minInput = inputs.find(i => i.getAttribute('name') === 'minPrice');
        const maxInput = inputs.find(i => i.getAttribute('name') === 'maxPrice');

        expect(minInput).toBeInTheDocument();
        expect(minInput).toHaveValue(50);

        expect(maxInput).toBeInTheDocument();
        expect(maxInput).toHaveValue(500);
    });

    it('should calls onChange when each input changes', async () => {
        render(<PriceFilterSection {...defaultProps} />);
        const inputs = screen.getAllByRole('spinbutton');
        const minInput = inputs.find(i => i.getAttribute('name') === 'minPrice');
        const maxInput = inputs.find(i => i.getAttribute('name') === 'maxPrice');

        await userEvent.clear(minInput);
        await userEvent.type(minInput, '123');
        expect(defaultProps.onChange).toHaveBeenCalled();

        defaultProps.onChange.mockClear();

        await userEvent.clear(maxInput);
        await userEvent.type(maxInput, '456');
        expect(defaultProps.onChange).toHaveBeenCalled();
    });

    it('should displays the error message when error prop is provided', () => {
        render(<PriceFilterSection {...defaultProps} error="Invalid range" />);
        const errorNode = screen.getByText('Invalid range');
        expect(errorNode).toBeInTheDocument();
        expect(errorNode).toHaveClass('Error');
    });

    it('should does not render an error when error prop is empty', () => {
        render(<PriceFilterSection {...defaultProps} error="" />);
        expect(screen.queryByText('Invalid range')).toBeNull();
    });
});
