import React from 'react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

// Mock the useTranslation hook from react-i18next
jest.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key) => {
      const translations = {
        'app.game.filter.search.title': 'Search...',
        'app.game.filter.search.button': 'Search',
      };
      return translations[key] || key;
    },
  }),
}));

import Search from './Search';

describe('Search Component', () => {
  it('should render input with correct placeholder and button with correct text', () => {
    render(<Search />);

    // Verify input placeholder
    const inputElement = screen.getByPlaceholderText('Search...');
    expect(inputElement).toBeInTheDocument();

    // Verify button text
    const buttonElement = screen.getByRole('button', { name: 'Search' });
    expect(buttonElement).toBeInTheDocument();
  });

  it('should call handleSearchChange on input change', () => {
    const handleSearchChange = jest.fn();
    render(<Search handleSearchChange={handleSearchChange} />);

    const inputElement = screen.getByPlaceholderText('Search...');
    userEvent.type(inputElement, 'test query');

    expect(handleSearchChange).toHaveBeenCalledTimes(10); // 'test query' has 10 characters
  });

  it('should call handleSearch on button click', () => {
    const handleSearch = jest.fn();
    render(<Search handleSearch={handleSearch} />);

    const buttonElement = screen.getByRole('button', { name: 'Search' });
    userEvent.click(buttonElement);

    expect(handleSearch).toHaveBeenCalledTimes(1);
  });
});
