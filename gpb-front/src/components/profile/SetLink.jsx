import React, { useEffect } from 'react'; // Import useEffect from React

import { useParams, useNavigate } from 'react-router-dom';
import { isUserAuth, setLinkToken } from '@util/authService';
import { accountLinkRequest } from '@services/userRequests';

const SetLink = () => {
    const navigate = useNavigate();
    const { token } = useParams();

    console.info(token)

    if (isUserAuth()) {
        accountLinkRequest(token, null, navigate);
    } else {
        setLinkToken(token);
    }

    useEffect(() => {
        navigate('/');
    }, []);
};

export default SetLink;
