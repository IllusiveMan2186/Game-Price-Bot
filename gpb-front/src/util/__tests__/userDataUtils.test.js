import { getLocale, setLocale } from '../userDataUtils';

const LOCALE_KEY = 'LOCALE';

describe('Locale utilities', () => {
    beforeEach(() => {
        localStorage.clear();
        jest.spyOn(console, 'info').mockImplementation(() => { });
    });

    afterEach(() => {
        jest.clearAllMocks();
    });

    describe('getLocale', () => {
        it('returns null if no locale is set', () => {
            expect(getLocale()).toBeNull();
        });

        it('returns the correct locale when set', () => {
            localStorage.setItem(LOCALE_KEY, 'en');
            expect(getLocale()).toBe('en');
        });
    });

    describe('setLocale', () => {
        it('should sets the locale in localStorage', () => {
            setLocale('fr');
            expect(localStorage.getItem(LOCALE_KEY)).toBe('fr');
            expect(console.info).toHaveBeenCalledWith('Locale set to: fr');
        });

        it('should removes the locale if called with falsy value', () => {
            localStorage.setItem(LOCALE_KEY, 'en');
            setLocale(null);
            expect(localStorage.getItem(LOCALE_KEY)).toBeNull();
            expect(console.info).toHaveBeenCalledWith('Locale set to: null');
        });
    });
});
