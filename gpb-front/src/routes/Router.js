import * as React from 'react';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Header from '../components/common/layout/Header/Header';
import AuthViewSwitcher from '../components/auth/AuthViewSwitcher';
import GameList from '../components/game/GameList'
import GameInfo from '../components/game/GameInfo';
import PasswordChange from '../components/profile/PasswordChange';
import EmailChange from '../components/profile/EmailChange';

export default function AppContent() {

    return (
        <>
            <BrowserRouter>
                <Header />
                <Routes>
                    <Route path="/" >
                        <Route index element={<GameList mode={"list"}  />} />
                        <Route path="/games/(url)?/:url?" element={<GameList mode={"list"} />} />
                        <Route path="/search/:searchName/(url)?/:url?" element={<GameList mode={"search"} />} />
                        <Route path="/user/games/(url)?/:url?" element={<GameList mode={"usersGames"}/>} />
                        <Route path="/game/:gameId" element={<GameInfo />} />
                        <Route path="/login" element={<AuthViewSwitcher />} />
                        <Route path="/change/email" element={<EmailChange />} />
                        <Route path="/change/password" element={<PasswordChange />} />
                    </Route>
                </Routes>
            </BrowserRouter>
        </>
    );
}