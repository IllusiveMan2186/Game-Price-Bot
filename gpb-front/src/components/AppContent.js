import * as React from 'react';
import { BrowserRouter, Routes, Route } from "react-router-dom";

import Header from './Header';
import Login from './Login';
import GameList from './GameList'
import GameInfo from './GameInfo';

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
                    </Route>
                </Routes>
            </BrowserRouter>
        </>
    );
}