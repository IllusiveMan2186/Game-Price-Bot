import React from 'react';
import { render, screen } from '@testing-library/react';
import Message from '../message';

jest.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key) => key,
    }),
}));

describe('Message component', () => {
    it('should renders translated message based on provided string', () => {
        render(<Message string="app.test.message" />);

        expect(screen.getByText('app.test.message')).toBeInTheDocument();
    });
});
