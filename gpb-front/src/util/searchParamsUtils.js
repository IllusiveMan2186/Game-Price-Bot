import { paramsStore } from '@store/store';
import * as constants from '@util/constants';

export const buildSearchParams = (params = paramsStore.getState().params) => {
    const {
        mode,
        genres,
        types,
        sortBy,
        minPrice,
        maxPrice,
        pageNum,
        pageSize
    } = params;

    const searchParams = new URLSearchParams();
    if (mode === "list") {

        if (Array.isArray(genres) && genres.length)
            searchParams.set('genre', genres.join(','));

        if (Array.isArray(types) && types.length)
            searchParams.set('type', types.join(','));

        if (typeof minPrice === 'number' && minPrice !== 0)
            searchParams.set('minPrice', minPrice);

        if (typeof maxPrice === 'number' && maxPrice !== 10000)
            searchParams.set('maxPrice', maxPrice);
    }

    if (sortBy && sortBy !== constants.sortsOptions[0].value)
        searchParams.set('sortBy', sortBy);

    if (pageSize && Number(pageSize) !== Number(constants.pageSizesOptions[0].value)) {

        searchParams.set('pageSize', pageSize);
    }

    if (typeof pageNum === 'number' && pageNum !== 1)
        searchParams.set('pageNum', pageNum);

    return searchParams.toString();
};