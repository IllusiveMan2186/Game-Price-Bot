import React, { useEffect } from 'react'; // Import useEffect from React

import { useParams, useNavigate } from 'react-router-dom';
import { setLinkToken } from '@util/userDataUtils';
import { useIsUserAuth } from '@util/authHook';
import { accountLinkRequest } from '@services/linkRequests';

const SetLink = () => {
    const navigate = useNavigate();
    const { token } = useParams();

    if (useIsUserAuth()) {
        accountLinkRequest(token, null, navigate);
    } else {
        setLinkToken(token);
    }

    useEffect(() => {
        navigate('/');
    }, []);
};

export default SetLink;
