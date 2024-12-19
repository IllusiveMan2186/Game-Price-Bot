
import { useSearchParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import { activateUserAccountRequest } from '@services/userRequests';

const ActivationPage = () => {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');

    activateUserAccountRequest(token, navigate);

    return (
        <div>
            <h1>Activating your account...</h1>
        </div>
    );
};

export default ActivationPage;
