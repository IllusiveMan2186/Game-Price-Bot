import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';

import Search from './Search';
import paramsReducer from '@features/params/paramsSlice';

jest.mock('react-i18next', () => ({ useTranslation: () => ({ t: (key) => key }) }));
jest.mock('@contexts/NavigationContext', () => ({ useNavigation: jest.fn() }));
jest.mock('@hooks/game/useGameActions', () => ({ useGameActions: jest.fn() }));
jest.mock('@util/searchParamsUtils', () => ({ __esModule: true, buildSearchParams: jest.fn() }));
jest.mock('react-notifications', () => ({ NotificationManager: { error: jest.fn() } }));
jest.mock('@util/message', () => ({ __esModule: true, default: ({ string }) => string }));

import { useNavigation } from '@contexts/NavigationContext';
import { useGameActions } from '@hooks/game/useGameActions';
import { buildSearchParams } from '@util/searchParamsUtils';
import { NotificationManager } from 'react-notifications';

describe('Search', () => {
  let store;
  const mockNavigate = jest.fn();
  const mockGetGameByUrlRequest = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    useNavigation.mockReturnValue(mockNavigate);
    useGameActions.mockReturnValue({ getGameByUrlRequest: mockGetGameByUrlRequest });

    store = configureStore({
      reducer: { params: paramsReducer },
      preloadedState: { params: { search: '' } },
    });
  });

  const renderSearch = () =>
    render(
      <Provider store={store}>
        <Search />
      </Provider>
    );

  it('should renders input with correct placeholder and button', () => {
    renderSearch();
    expect(screen.getByPlaceholderText('app.game.filter.search.title')).toBeInTheDocument();
    expect(screen.getByText('app.game.filter.search.button')).toBeInTheDocument();
  });

  it('should updates Redux search on input change', async () => {
    renderSearch();
    const input = screen.getByPlaceholderText('app.game.filter.search.title');
    await userEvent.type(input, 'hello');
    expect(store.getState().params.search).toBe('hello');
  });

  it('should shows error when search is empty', async () => {
    renderSearch();
    const button = screen.getByText('app.game.filter.search.button');
    await userEvent.click(button);
    expect(NotificationManager.error).toHaveBeenCalledTimes(1);
    const [msgEl, titleEl] = NotificationManager.error.mock.calls[0];
    expect(msgEl.props.string).toBe('app.game.error.name.empty');
    expect(titleEl.props.string).toBe('app.game.error.title');
  });

  it('should calls getGameByUrlRequest for valid URL search', async () => {
    renderSearch();
    const input = screen.getByPlaceholderText('app.game.filter.search.title');
    await userEvent.type(input, 'https://test.com');
    const button = screen.getByText('app.game.filter.search.button');
    await userEvent.click(button);
    expect(mockGetGameByUrlRequest).toHaveBeenCalledWith('https://test.com', mockNavigate);
  });

  it('should navigates to search page for valid name search', async () => {
    buildSearchParams.mockReturnValue('sortBy=name&pageSize=10&pageNum=2');
    renderSearch();
    const input = screen.getByPlaceholderText('app.game.filter.search.title');
    await userEvent.type(input, 'GameName');
    const button = screen.getByText('app.game.filter.search.button');
    await userEvent.click(button);
    expect(mockNavigate).toHaveBeenNthCalledWith(1, '/search/GameName?sortBy=name&pageSize=10&pageNum=2');
    expect(mockNavigate).toHaveBeenNthCalledWith(2, 0);
  });
});
