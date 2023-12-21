import * as React from 'react';
import Localization from './Localization';
import Message from '../../util/message';

export default class Footer extends React.Component {

    render() {
        return (
            <>
                {/* Remove the container if you want to extend the Footer to full width. */}
                {/* Footer */}
                <footer
                    className="text-center text-lg-start text-white"
                    style={{ backgroundColor: "#929fba", marginBottom: '0px' }}
                >
                    {/* Grid container */}
                    <div className="container p-4 pb-0">
                        {/* Section: Links */}
                        <section className="">
                            {/*Grid row*/}
                            <div className="row">
                                {/* Grid column */}
                                <div className="col-md-3 col-lg-3 col-xl-3 mx-auto mt-3">
                                    <h6 className="text-uppercase mb-4 font-weight-bold">
                                        Game Price Bot
                                    </h6>
                                    <p>
                                        <Message string={'app.footer.gpb.info'} />
                                    </p>
                                </div>
                                <div className="col-md-4 col-lg-3 col-xl-3 mx-auto mt-3">
                                    <h6 className="text-uppercase mb-4 font-weight-bold"><Message string={'app.footer.contact'} /></h6>
                                    <p>
                                        <i className="fas fa-envelope mr-3" /> info@gmail.com
                                    </p>
                                </div>
                                {/* Grid column */}
                                {/* Grid column */}
                                <div className="col-md-3 col-lg-2 col-xl-2 mx-auto mt-3">
                                    <h6 className="text-uppercase mb-4 font-weight-bold">
                                        <Message string={'app.footer.follow'} />
                                    </h6>
                                    {/* Google */}
                                    <a
                                        className="btn btn-primary btn-floating m-1"
                                        style={{ backgroundColor: "#dd4b39" }}
                                        href="#!"
                                        role="button"
                                    >
                                        <i className="fab fa-google" />
                                    </a>
                                    {/* Linkedin */}
                                    <a
                                        className="btn btn-primary btn-floating m-1"
                                        style={{ backgroundColor: "#0082ca" }}
                                        href="#!"
                                        role="button"
                                    >
                                        <i className="fab fa-linkedin-in" />
                                    </a>
                                    {/* Github */}
                                    <a
                                        className="btn btn-primary btn-floating m-1"
                                        style={{ backgroundColor: "#333333" }}
                                        href="#!"
                                        role="button"
                                    >
                                        <i className="fab fa-github" />
                                    </a>
                                </div>
                                {<Localization />}
                            </div>
                            {/*Grid row*/}
                        </section>
                        {/* Section: Links */}
                    </div>
                    {/* Grid container */}
                    {/* Copyright */}
                    <div
                        className="text-center p-3"
                        style={{ backgroundColor: "rgba(0, 0, 0, 0.2)" }}
                    >
                        © 2020 Copyright:
                        <a className="text-white" href="https://mdbootstrap.com/">
                            MDBootstrap.com
                        </a>
                    </div>
                    {/* Copyright */}
                </footer>
                {/* Footer */}
                {/* End of .container */}
            </>


        );
    }
};