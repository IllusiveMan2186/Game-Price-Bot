import * as React from 'react';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Header from './Header';
import Login from '../auth/Login';
import GameList from '../game/GameList'
import GameInfo from '../game/GameInfo';
import PasswordChange from '../profile/PasswordChange';
import EmailChange from '../profile/EmailChange';

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
                        <Route path="/login" element={<Login />} />
                        <Route path="/change/email" element={<EmailChange />} />
                        <Route path="/change/password" element={<PasswordChange />} />
                    </Route>
                </Routes>
            </BrowserRouter>
        </>
    );
}