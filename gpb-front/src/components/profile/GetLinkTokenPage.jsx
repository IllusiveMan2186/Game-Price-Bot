import React, { useState } from 'react';
import Message from '@util/message';
import { getLinkTokenRequest } from '@services/userRequests';
import { useNavigate } from 'react-router-dom';

const GetLinkTokenPage = () => {
    const [token, setToken] = useState('');
    const navigate = useNavigate();

    if (!token) {
        getLinkTokenRequest(setToken, navigate);
    }

    const copyToClipboard = () => {
        navigator.clipboard.writeText(token);
    };

    return (
        <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
            <h1><Message string={"app.user.get.link.token.title"} /></h1>
            <label className="form-label">
                <Message string={"app.user.get.link.token.description"} />
            </label>
            <div style={{ marginTop: '20px' }}>
                <button
                    className={"btn btn-primary"}
                    onClick={copyToClipboard}
                    style={{ padding: '10px 20px', cursor: 'pointer' }}
                >
                    {token}
                </button>
            </div>
        </div>
    );
};

export default GetLinkTokenPage;
