import { paramsStore } from '@store/store';

import { buildSearchParams } from '@util/searchParamsUtils';


export const reloadPage = (navigate, params = paramsStore.getState().params) => {
    const query = buildSearchParams(params);
    const path =
        params.mode === 'usersGames' ? '/user/games/' :
            params.mode === 'search' ? `/search/${params.search}/` :
                '/games/';

    navigate(`${path}${query}`);
    navigate(0);
};

