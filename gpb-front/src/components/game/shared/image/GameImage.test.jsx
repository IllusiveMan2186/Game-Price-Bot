import React from 'react';
import { render, screen } from '@testing-library/react';
import GameImage from './GameImage';

// Mock the config module
jest.mock('@root/config', () => ({
  BACKEND_SERVICE_URL: 'http://backend-service'
}));

describe('GameImage', () => {
  test('renders image with correct src, alt, and className', () => {
    const gameName = 'Test:Game/Name';
    const className = 'custom-class';

    render(<GameImage gameName={gameName} className={className} />);

    const img = screen.getByRole('img');
    expect(img).toHaveAttribute('src', 'http://backend-service/game/image/Test_Game_Name');
    expect(img).toHaveAttribute('alt', gameName);
    expect(img).toHaveClass(className);
  });

  test('renders image with unsanitized gameName when no special characters', () => {
    const gameName = 'SimpleGame';
    render(<GameImage gameName={gameName} />);

    const img = screen.getByRole('img');
    expect(img).toHaveAttribute('src', 'http://backend-service/game/image/SimpleGame');
    expect(img).toHaveAttribute('alt', gameName);
  });
});
