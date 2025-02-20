import * as React from 'react';
import Localization from '@components/common/Localization';
import Messenger from '@components/common/messenger/Messenger';
import Message from '@util/message';

import config from "@root/config";

const Footer = () => {
    return (
        <>
            <footer
                className="text-center text-lg-start text-white"
                style={{ backgroundColor: "#929fba", marginBottom: '0px' }}
            >
                <div className="container p-4 pb-0">
                    <section>
                        <div className="row">
                            <div id="footer-description" className="col-md-3 col-lg-3 col-xl-3 mx-auto mt-3">
                                <h6 className="text-uppercase mb-4 font-weight-bold">
                                    Game Price Bot
                                </h6>
                                <p>
                                    <Message string={'app.footer.gpb.info'} />
                                </p>
                            </div>
                            <div id="footer-contact-info" className="col-md-4 col-lg-3 col-xl-3 mx-auto mt-3">
                                <h6 className="text-uppercase mb-4 font-weight-bold">
                                    <Message string={'app.footer.contact'} />
                                </h6>
                                <p>
                                    <i className="fas fa-envelope mr-3" /> {config.SUPPORT_EMAIL}
                                </p>
                            </div>
                            <Messenger />
                            <Localization />
                        </div>
                    </section>
                </div>
            </footer>
        </>
    );
};

export default Footer;
