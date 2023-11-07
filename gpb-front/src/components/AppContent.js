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
                        <Route index element={<GameList />} />
                        <Route path="/games/(url)?/:url?" element={<GameList isSearch={false} />} />
                        <Route path="/search/:searchName/(url)?/:url?" element={<GameList isSearch={true} />} />
                        <Route path="/game/:gameId" element={<GameInfo />} />
                        <Route path="/login" element={<Login />} />
                    </Route>
                </Routes>
            </BrowserRouter>
        </>
    );
}