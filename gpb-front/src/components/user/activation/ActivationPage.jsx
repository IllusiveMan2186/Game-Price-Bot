import { useSearchParams } from 'react-router-dom';
import { useActivationActions } from '@hooks/user/useActivationActions';
import { useEffect } from 'react';

const ActivationPage = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');
    const { activateUserAccountRequest } = useActivationActions();

    useEffect(() => {
        if (token) {
            activateUserAccountRequest(token);
        }
    }, [token, activateUserAccountRequest]);

    return (
        <div>
            <h1>Activating your account...</h1>
        </div>
    );
};

export default ActivationPage;
