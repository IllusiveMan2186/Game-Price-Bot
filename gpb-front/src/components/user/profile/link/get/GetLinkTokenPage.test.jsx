import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import GetLinkTokenPage from './GetLinkTokenPage';

// Mock Message to simply render the provided string.
jest.mock('@util/message', () => ({
  __esModule: true,
  default: ({ string }) => <span>{string}</span>
}));

// Mock useLinkActions hook.
jest.mock('@hooks/user/useLinkActions', () => ({
  useLinkActions: jest.fn(),
}));

describe('GetLinkTokenPage', () => {
  let getLinkTokenRequestMock;

  beforeEach(() => {
    // Reset the mock before each test.
    getLinkTokenRequestMock = jest.fn();
    const { useLinkActions } = require('@hooks/user/useLinkActions');
    useLinkActions.mockReturnValue({
      getLinkTokenRequest: getLinkTokenRequestMock,
    });
  });

  test('calls getLinkTokenRequest when token is empty', () => {
    render(<GetLinkTokenPage />);
    // Expect getLinkTokenRequest to be called since token is initially empty.
    expect(getLinkTokenRequestMock).toHaveBeenCalled();
  });

  test('displays token in button and copies it to clipboard when clicked', async () => {
    // Simulate getLinkTokenRequest setting the token.
    getLinkTokenRequestMock.mockImplementation((setToken) => {
      setToken('my-token');
    });

    // Mock the clipboard.writeText method.
    const writeTextMock = jest.fn();
    Object.assign(navigator, {
      clipboard: { writeText: writeTextMock },
    });

    render(<GetLinkTokenPage />);

    // Wait until the token is updated and rendered inside the button.
    await waitFor(() => {
      expect(screen.getByRole('button')).toHaveTextContent('my-token');
    });

    // Click the button.
    const button = screen.getByRole('button');
    fireEvent.click(button);

    // Verify that the clipboard's writeText function was called with the token.
    expect(writeTextMock).toHaveBeenCalledWith('my-token');
  });
});
