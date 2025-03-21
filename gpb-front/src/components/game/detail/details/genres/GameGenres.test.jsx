// GameGenres.test.jsx
import React from 'react';
import { render } from '@testing-library/react';
import GameGenres from './GameGenres';

// Mock the Message component
jest.mock('@util/message', () => ({ string }) => <span>{string}</span>);

describe('GameGenres Component', () => {
  it('renders genre title and list of genres', () => {
    const genres = ['Action', 'Adventure'];
    const { getByText } = render(<GameGenres genres={genres} />);

    // Check title
    expect(getByText('app.game.filter.genre.title')).toBeInTheDocument();

    // Check genres
    genres.forEach((genre) => {
      expect(getByText(`app.game.genre.${genre.toLowerCase()}`)).toBeInTheDocument();
    });
  });

  it('renders only the title if no genres are provided', () => {
    const { container, getByText } = render(<GameGenres genres={[]} />);
    
    // Title should be rendered
    expect(getByText('app.game.filter.genre.title')).toBeInTheDocument();
    
    // No genre spans should be rendered
    expect(container.querySelectorAll('.app-game__genre-subtext').length).toBe(0);
  });
});
