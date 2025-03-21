import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Router from './Router';

// Mock routed components with unique test ids.
jest.mock('@components/common/layout/header/Header', () => () => <div data-testid="header">Header</div>);
jest.mock('@components/user/activation/ActivationPage', () => () => <div data-testid="activation-page">ActivationPage</div>);
jest.mock('@components/user/auth/AuthViewSwitcher', () => () => <div data-testid="auth-view-switcher">AuthViewSwitcher</div>);
jest.mock('@components/game/list/GameListPage', () => (props) => (
  <div data-testid="game-list-page">GameListPage {props.mode}</div>
));
jest.mock('@components/game/detail/GameDetailPage', () => () => <div data-testid="game-detail-page">GameDetailPage</div>);
jest.mock('@components/user/profile/password/PasswordChange', () => () => <div data-testid="password-change">PasswordChange</div>);
jest.mock('@components/user/profile/email/EmailChange', () => () => <div data-testid="email-change">EmailChange</div>);
jest.mock('@components/user/profile/link/set/SetLink', () => () => <div data-testid="set-link">SetLink</div>);
jest.mock('@components/user/profile/link/page/LinkPage', () => () => <div data-testid="link-page">LinkPage</div>);
jest.mock('@components/user/profile/link/get/GetLinkTokenPage', () => () => <div data-testid="get-link-token-page">GetLinkTokenPage</div>);
jest.mock('@components/common/error/ErrorPage', () => () => <div data-testid="error-page">ErrorPage</div>);
jest.mock('@components/user/profile/email/confirm/EmailChangeConfirm', () => () => <div data-testid="email-change-confirm">EmailChangeConfirm</div>);

describe('Router', () => {
  test('renders Header and GameListPage with mode "list" for "/" route', () => {
    render(
      <MemoryRouter initialEntries={['/']}>
        <Router />
      </MemoryRouter>
    );

    // Header is always rendered.
    expect(screen.getByTestId('header')).toBeInTheDocument();

    // Home route ("/") should render GameListPage with mode "list".
    const gameList = screen.getByTestId('game-list-page');
    expect(gameList).toBeInTheDocument();
    expect(gameList).toHaveTextContent('list');
  });

  test('renders GameListPage with mode "list" for "/games/:url?" route', () => {
    render(
      <MemoryRouter initialEntries={['/games/extra']}>
        <Router />
      </MemoryRouter>
    );
    const gameList = screen.getByTestId('game-list-page');
    expect(gameList).toBeInTheDocument();
    expect(gameList).toHaveTextContent('list');
  });

  test('renders GameListPage with mode "search" for "/search/:searchName/:url?" route', () => {
    render(
      <MemoryRouter initialEntries={['/search/test/extra']}>
        <Router />
      </MemoryRouter>
    );
    const gameList = screen.getByTestId('game-list-page');
    expect(gameList).toBeInTheDocument();
    expect(gameList).toHaveTextContent('search');
  });

  test('renders GameListPage with mode "usersGames" for "/user/games/:url?" route', () => {
    render(
      <MemoryRouter initialEntries={['/user/games/extra']}>
        <Router />
      </MemoryRouter>
    );
    const gameList = screen.getByTestId('game-list-page');
    expect(gameList).toBeInTheDocument();
    expect(gameList).toHaveTextContent('usersGames');
  });

  test('renders GameDetailPage for "/game/:gameId" route', () => {
    render(
      <MemoryRouter initialEntries={['/game/123']}>
        <Router />
      </MemoryRouter>
    );
    expect(screen.getByTestId('game-detail-page')).toBeInTheDocument();
  });

  test('renders AuthViewSwitcher for "/login" route', () => {
    render(
      <MemoryRouter initialEntries={['/login']}>
        <Router />
      </MemoryRouter>
    );
    expect(screen.getByTestId('auth-view-switcher')).toBeInTheDocument();
  });

  test('renders EmailChange for "/change/email" route', () => {
    render(
      <MemoryRouter initialEntries={['/change/email']}>
        <Router />
      </MemoryRouter>
    );
    expect(screen.getByTestId('email-change')).toBeInTheDocument();
  });

  test('renders PasswordChange for "/change/password" route', () => {
    render(
      <MemoryRouter initialEntries={['/change/password']}>
        <Router />
      </MemoryRouter>
    );
    expect(screen.getByTestId('password-change')).toBeInTheDocument();
  });

  test('renders ActivationPage for "/activation" route', () => {
    render(
      <MemoryRouter initialEntries={['/activation']}>
        <Router />
      </MemoryRouter>
    );
    expect(screen.getByTestId('activation-page')).toBeInTheDocument();
  });

  test('renders LinkPage for "/link" route', () => {
    render(
      <MemoryRouter initialEntries={['/link']}>
        <Router />
      </MemoryRouter>
    );
    expect(screen.getByTestId('link-page')).toBeInTheDocument();
  });

  test('renders GetLinkTokenPage for "/link/token" route', () => {
    render(
      <MemoryRouter initialEntries={['/link/token']}>
        <Router />
      </MemoryRouter>
    );
    expect(screen.getByTestId('get-link-token-page')).toBeInTheDocument();
  });

  test('renders SetLink for "/token/:token" route', () => {
    render(
      <MemoryRouter initialEntries={['/token/abc']}>
        <Router />
      </MemoryRouter>
    );
    expect(screen.getByTestId('set-link')).toBeInTheDocument();
  });

  test('renders EmailChangeConfirm for "/email/change/confirm" route', () => {
    render(
      <MemoryRouter initialEntries={['/email/change/confirm']}>
        <Router />
      </MemoryRouter>
    );
    expect(screen.getByTestId('email-change-confirm')).toBeInTheDocument();
  });

  test('renders ErrorPage for "/error" route', () => {
    render(
      <MemoryRouter initialEntries={['/error']}>
        <Router />
      </MemoryRouter>
    );
    expect(screen.getByTestId('error-page')).toBeInTheDocument();
  });
});
