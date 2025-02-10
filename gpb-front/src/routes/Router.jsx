import * as React from 'react';
import { Routes, Route } from "react-router-dom";

import Header from '@components/common/layout/header/Header';
import ActivationPage from '@components/user/activation/ActivationPage';
import AuthViewSwitcher from '@components/user/auth/AuthViewSwitcher';
import GameListPage from '@components/game/list/GameListPage';
import GameDetailPage from '@components/game/detail/GameDetailPage';
import PasswordChange from '@components/user/profile/PasswordChange';
import EmailChange from '@components/user/profile/EmailChange';
import SetLink from '@components/user/profile/SetLink';
import LinkPage from '@components/user/profile/LinkPage';
import GetLinkTokenPage from '@components/user/profile/GetLinkTokenPage';

export default function AppContent() {

    return (
        <>
            <Header />
            <Routes>
                <Route path="/" >
                    <Route path="/" element={<GameListPage mode="list" />} />

                    {/* Games list with optional URL */}
                    <Route path="/games/:url?" element={<GameListPage mode="list" />} />

                    {/* Search results with searchName and optional URL */}
                    <Route path="/search/:searchName/:url?" element={<GameListPage mode="search" />} />

                    {/* User's games list with optional URL */}
                    <Route path="/user/games/:url?" element={<GameListPage mode="usersGames" />} />

                    {/* Game details */}
                    <Route path="/game/:gameId" element={<GameDetailPage />} />

                    {/* Authentication */}
                    <Route path="/login" element={<AuthViewSwitcher />} />

                    {/* Profile changes */}
                    <Route path="/change/email" element={<EmailChange />} />
                    <Route path="/change/password" element={<PasswordChange />} />

                    <Route path="/activation?" element={<ActivationPage />} />

                    <Route path="/link" element={<LinkPage />} />
                    <Route path="/link/token" element={<GetLinkTokenPage />} />
                    <Route path="/token/:token" element={<SetLink />} />
                </Route>
            </Routes>
        </>
    );
}