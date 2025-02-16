import { useEffect } from 'react'; // Import useEffect from React

import { useParams } from 'react-router-dom';

import { useNavigation } from "@contexts/NavigationContext";
import { useAuth } from "@contexts/AuthContext";
import { useLinkActions } from '@hooks/user/useLinkActions';

const SetLink = () => {
    const navigate = useNavigation();
    const { accountLinkRequest } = useLinkActions();

    const { isUserAuth, setLinkToken } = useAuth();
    const { token } = useParams();

    if (isUserAuth()) {
        accountLinkRequest(token);
    } else {
        setLinkToken(token);
    }

    useEffect(() => {
        navigate('/');
    }, []);
};

export default SetLink;
