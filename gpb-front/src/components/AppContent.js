import * as React from 'react';
import logo from '../logo.svg';

import { request, setAuthHeader } from '../helpers/axios_helper';

import Buttons from './Buttons';
import AuthContent from './AuthContent';
import LoginForm from './LoginForm';
import WelcomeContent from './WelcomeContent'

export default class AppContent extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            componentToShow: "welcome"
        }
    };

    login = () => {
        this.setState({ componentToShow: "login" })
    };

    logout = () => {
        this.setState({ componentToShow: "welcome" })
        setAuthHeader(null);
    };

    onLogin = (e, username, password) => {
        e.preventDefault();
        request(
            "POST",
            "/login",
            {
                username: username,
                password: password
            }).then(
                (response) => {
                    setAuthHeader(response.data.token);
                    this.setState({ componentToShow: "messages" });
                }).catch(
                    (error) => {
                        setAuthHeader(null);
                        this.setState({ componentToShow: "welcome" })
                    }
                );
    };

    onRegister = (event, username, password) => {
        event.preventDefault();
        request(
            "POST",
            "/register",
            {
                username: username,
                password: password
            }).then(
                (response) => {
                    setAuthHeader(response.data.token);
                    this.setState({ componentToShow: "messages" });
                }).catch(
                    (error) => {
                        setAuthHeader(null);
                        this.setState({ componentToShow: "welcome" })
                    }
                );
    };

    render() {
        return (
            <>
                <header className="App-header">
                    <div className="App-title">
                        <img src={logo} className="App-logo" alt="logo" />
                        <h1 style={{fontSize: 'auto'}}>GPB</h1>
                    </div>

                    <Buttons
                        login={this.login}
                        logout={this.logout}
                    />
                </header>

                {this.state.componentToShow === "welcome" && <WelcomeContent />}
                {this.state.componentToShow === "login" && <LoginForm onLogin={this.onLogin} onRegister={this.onRegister} />}
                {this.state.componentToShow === "messages" && <AuthContent />}

            </>
        );
    };
}