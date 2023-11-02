import * as React from 'react';
import logo from '../logo.svg';

import { request, setAuthHeader } from '../helpers/axios_helper';

import Buttons from './Buttons';
import AuthContent from './AuthContent';
import LoginForm from './LoginForm';
import Games from './Games'
import Message from './Message';
import GameInfo from './GameInfo';

export default class AppContent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            componentToShow: "game",
            game: null,
            errorMessage: ""
        }
    };

    login = () => {
        this.setState({ componentToShow: "login" })
    };

    logout = () => {
        this.setState({ componentToShow: "game" })
        setAuthHeader(null);
    };

    getErrorMessage = () => {
        return this.state.errorMessage
    }

    cleanErrorMessage = () => {
        this.setState({ 'errorMessage': "" })
    }

    showGames = () => {
        this.setState({ componentToShow: "game" })
    }

    getGameInfo = (gameId) => {
        request(
            "GET",
            "/game/" + gameId,
        ).then(
            (response) => {
                this.state.game = response.data;
                this.setState({ componentToShow: "gameInfo" })
            }).catch(
                (error) => {
                }
            );

    }

    onLogin = (e, email, password) => {
        e.preventDefault();
        request(
            "POST",
            "/login",
            {
                email: email,
                password: password
            }).then(
                (response) => {
                    setAuthHeader(response.data.token);
                    this.setState({ componentToShow: "messages" });
                }).catch(
                    (error) => {
                        setAuthHeader(null);
                        this.setState({ errorMessage: <Message string={error.response.data} /> })
                    }
                );
    };

    onRegister = (event, email, password) => {
        event.preventDefault();
        request(
            "POST",
            "/registration",
            {
                email: email,
                password: password
            }).then(
                (response) => {
                    setAuthHeader(response.data.token);
                    this.setState({ componentToShow: "messages" });
                }).catch(
                    (error) => {
                        setAuthHeader(null);
                        this.setState({ errorMessage: <Message string={error.response.data} /> })
                    }
                );
    };

    render() {
        return (
            <>
                <header className="App-header">
                    <div className="App-title" onClick={this.showGames}>
                        <img src={logo} className="App-logo" alt="logo" />
                        <h1 style={{ fontSize: 'auto' }}>GPB</h1>
                    </div>

                    <Buttons
                        login={this.login}
                        logout={this.logout}
                    />
                </header>

                {this.state.componentToShow === "game" && <Games getGameInfo={this.getGameInfo} />}
                {this.state.componentToShow === "gameInfo" && <GameInfo game={this.state.game} />}
                {this.state.componentToShow === "login" && <LoginForm onLogin={this.onLogin} onRegister={this.onRegister} errorMessage={this.getErrorMessage}
                    cleanErrorMessage={this.cleanErrorMessage} />}
                {this.state.componentToShow === "messages" && <AuthContent />}

            </>
        );
    };
}