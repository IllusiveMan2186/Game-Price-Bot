import * as React from 'react'
import classNames from 'classnames'
import Message from './Message';

export default class LoginForm extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      active: "login",
      email: "",
      emailError: "",
      password: "",
      passwordError: "",
      confirmPassword: "",
      confirmPasswordError: "",
      errorMessage: props.errorMessage,
      cleanErrorMessage: props.cleanErrorMessage,
      onLogin: props.onLogin,
      onRegister: props.onRegister
    };
  };

  onChangeHandler = (event) => {
    let name = event.target.name;
    let value = event.target.value;
    this.setState({ [name]: value })
    this.validateInput(event);
  };

  onPasswordConfirmChangeHandler = (event) => {
    let name = event.target.name;
    let value = event.target.value;
    this.setState({ [name]: value })
  };

  onSubmitLogin = (e) => {
    this.state.onLogin(e, this.state.email, this.state.password)
  };

  onSubmitRegistration = (e) => {
    if (this.isRegistrationFormValid()) {
      this.state.onRegister(
        e,
        this.state.email,
        this.state.password);
    }

  }

  isRegistrationFormValid = () => {
    return this.isAllFieldsFiled() && this.isNoErrors()
  }

  cleanAll(string) {
    this.setState({
      active: string,
      email: "",
      emailError: "",
      password: "",
      passwordError: "",
      confirmPassword: "",
      confirmPasswordError: ""
    });
    this.state.cleanErrorMessage();
  }

  isLoginFormValid() {
    return !this.isEmptyString(this.state.email) && !this.isEmptyString(this.state.password)
  }


  isNoErrors() {
    return this.isEmptyString(this.state.emailError) && this.isEmptyString(this.state.passwordError) && this.isEmptyString(this.state.confirmPasswordError);
  }

  isAllFieldsFiled() {
    return !this.isEmptyString(this.state.email) && !this.isEmptyString(this.state.password) && !this.isEmptyString(this.state.confirmPassword);
  }

  isValidEmail(email) {
    return /\S+@\S+\.\S+/.test(email);
  }

  isEmptyString(string) {
    return string.trim() === "";
  }

  validateInput = e => {
    let name = e.target.name;
    let value = e.target.value;

    switch (name) {
      case "email":
        if (!value) {
          this.setState({ 'emailError': <Message string={'app.login.form.error.empty.email'} /> })
        } else if (!this.isValidEmail(value)) {
          this.setState({ 'emailError': <Message string={'app.login.form.error.wrong.email'} /> })
        } else {
          this.setState({ 'emailError': "" })
        }
        break;

      case "password":
        if (!value) {
          this.setState({ 'passwordError': <Message string={'app.login.form.error.empty.password'} /> })
        } else if (value !== this.state.password) {
          this.setState({ 'passwordError':  <Message string={'app.registr.form.error.not.match.pass.conf'} /> })
        } else {
          this.setState({ 'passwordError': "" })
        }
        break;

      case "confirmPassword":
        if (!value) {
          this.setState({ 'confirmPasswordError': <Message string={'app.registr.form.error.empty.pass.conf'} /> })
        } else if (this.state.password && value !== this.state.password) {
          this.setState({ 'confirmPasswordError': <Message string={'app.registr.form.error.not.match.pass.conf'} /> })
        } else {
          this.setState({ 'confirmPasswordError': "" })
        }
        break;

      default:
        break;
    }
  }


  render() {
    return (
      <div className="row justify-content-center">
        <div className="col-4">
          <ul className="nav nav-pills nav-justified mb-3" id="ex1" role="tablist">
            <li className="nav-item" role="presentation">
              <button className={classNames("nav-link", this.state.active === "login" ? "active" : "")} id="tab-login"
                onClick={() => this.cleanAll("login")}>{<Message string={'app.login'} />}</button>
            </li>
            <li className="nav-item" role="presentation">
              <button className={classNames("nav-link", this.state.active === "register" ? "active" : "")} id="tab-register"
                onClick={() => this.cleanAll("register")}><Message string={'app.registr'} /></button>
            </li>
          </ul>

          <span className='Error'>{this.state.errorMessage()}</span>
          <div className="tab-content">
            <div className={classNames("tab-pane", "fade", this.state.active === "login" ? "show active" : "")} id="pills-login" >
              <form onSubmit={this.onSubmitLogin}>

                <div className="form-outline mb-4">
                  <label className="form-label" htmlFor="loginName"><Message string={'app.login.form.email'} /></label>
                  <input type="email" id="loginName" name="email" className="form-control" onChange={this.onChangeHandler} />
                </div>

                <div className="form-outline mb-4">
                  <label className="form-label" htmlFor="loginPassword"><Message string={'app.login.form.password'} /></label>
                  <input type="password" id="loginPassword" name="password" className="form-control" onChange={this.onChangeHandler} />
                </div>

                <button type="submit" className="btn btn-primary btn-block mb-4" disabled={!this.isLoginFormValid()}><Message string={'app.login.form.singup'} /></button>

              </form>
            </div>
            <div className={classNames("tab-pane", "fade", this.state.active === "register" ? "show active" : "")} id="pills-register" >
              <form onSubmit={this.onSubmitRegistration}>


                <div className="form-outline mb-4">
                  <label className="form-label" htmlFor="email"><Message string={'app.login.form.email'} /></label>
                  <input type="email" id="login" name="email" className="form-control" onChange={this.onChangeHandler}
                    onBlur={this.validateInput} />
                  <span className='Error'>{this.state.emailError}</span>
                </div>

                <div className="form-outline mb-4">
                  <label className="form-label" htmlFor="registerPassword"><Message string={'app.login.form.password'} /></label>
                  <input type="password" id="registerPassword" name="password" className="form-control" onChange={this.onChangeHandler}
                    onBlur={this.validateInput} />
                  <span className='Error'>{this.state.passwordError}</span>
                </div>

                <div className="form-outline mb-4">
                  <label className="form-label" htmlFor="registerPasswordConfirm"><Message string={'app.registr.form.pass.conf'} /></label>
                  <input type="password" id="registerPasswordConfirmation" name="confirmPassword" className="form-control"
                    onChange={this.onChangeHandler} onBlur={this.validateInput} />
                  <span className='Error'>{this.state.confirmPasswordError}</span>
                </div>

                <button type="submit" className="btn btn-primary btn-block mb-3" disabled={!this.isRegistrationFormValid()}><Message string={'app.registr.form.reg.buttom'} /></button>
              </form>
            </div>
          </div>
        </div>
      </div>
    );
  }
}