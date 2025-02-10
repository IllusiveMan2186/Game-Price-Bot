import React from 'react';

import Message from '@util/message';
import { useIsUserAuth } from '@util/authHook';
import { getLinkTokenForMessengerRequest } from '@services/linkRequests';

import config from "@root/config";

import telegramImage from '@assets/images/telegram.png';

import './Messenger.css';

const Messenger = () => {

  const isUserAuth = useIsUserAuth();

  const onMessangerClick = (linkToMessanger, paramsForSync) => {
    if (isUserAuth) { 
      const url = linkToMessanger + paramsForSync;
      getLinkTokenForMessengerRequest(url);
    } else {
      window.open(linkToMessanger, '_blank');
    }
  };

  return (
    <div id="footer-language-choice" className="col-md-3 col-lg-2 col-xl-2 mx-auto mt-3">
      <h6 className="text-uppercase mb-4 font-weight-bold">
        <Message string={'app.footer.messenger'} />
      </h6>
      <button
        className="btn btn-primary btn-floating m-1 messenger-button"
        onClick={() => onMessangerClick(config.TELEGRAM_BOT_URL, '?start=')}
      >
        <img
          src={telegramImage}
          alt="Telegram Icon"
          className="messenger-button-icon"
        />
      </button>
    </div>
  );
};

export default Messenger;
