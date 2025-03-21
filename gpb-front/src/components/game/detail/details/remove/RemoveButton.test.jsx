import React from 'react';
import { render, fireEvent } from '@testing-library/react';
import RemoveButton from './RemoveButton';

jest.mock('@hooks/game/useGameActions', () => ({
  useGameActions: jest.fn(),
}));

jest.mock('@util/message', () => ({ string }) => <span>{string}</span>);

describe('RemoveButton Component', () => {
  const mockRemoveGameRequest = jest.fn();

  beforeEach(() => {
    require('@hooks/game/useGameActions').useGameActions.mockReturnValue({
      removeGameRequest: mockRemoveGameRequest,
    });
  });

  it('renders the button with message', () => {
    const { getByText } = render(<RemoveButton gameId={42} />);
    expect(getByText('app.game.info.remove')).toBeInTheDocument();
  });

  it('calls removeGameRequest with correct gameId on click', () => {
    const { getByRole } = render(<RemoveButton gameId={123} />);
    fireEvent.click(getByRole('button'));
    expect(mockRemoveGameRequest).toHaveBeenCalledWith(123);
  });
});
