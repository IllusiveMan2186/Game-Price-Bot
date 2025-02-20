
import { useSearchParams } from 'react-router-dom';
import { useNavigation } from "@contexts/NavigationContext";
import { useLinkActions } from '@hooks/user/useLinkActions';

const ActivationPage = () => {
    const navigate = useNavigation();
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token');
    const { activateUserAccountRequest } = useLinkActions();

    activateUserAccountRequest(token, navigate);

    return (
        <div>
            <h1>Activating your account...</h1>
        </div>
    );
};

export default ActivationPage;
