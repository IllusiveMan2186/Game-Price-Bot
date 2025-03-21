import React from 'react';
import { render, screen } from '@testing-library/react';
import Message from '../message';
import { useTranslation } from 'react-i18next';

jest.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key) => key, // directly return key for testing
    }),
}));

describe('Message component', () => {
    test('renders translated message based on provided string', () => {
        render(<Message string="app.test.message" />);

        expect(screen.getByText('app.test.message')).toBeInTheDocument();
    });
});
