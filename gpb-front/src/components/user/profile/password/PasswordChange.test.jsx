import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import PasswordChange from './PasswordChange';
import * as Yup from 'yup';

jest.mock('@util/message', () => ({
    __esModule: true,
    default: ({ string }) => <span>{string}</span>
}));

jest.mock('@hooks/user/useUserActions', () => ({
    useUserActions: jest.fn(),
}));
jest.mock('@hooks/user/useAuthActions', () => ({
    useAuthActions: jest.fn(),
}));
jest.mock('@contexts/AuthContext', () => ({
    useAuth: jest.fn(),
}));
jest.mock('@contexts/NavigationContext', () => ({
    useNavigation: jest.fn(),
}));

describe('PasswordChange', () => {
    let passwordChangeRequestMock;
    let userLogoutRequestMock;
    let navigateMock;

    beforeEach(() => {
        // Create fresh mocks for each test.
        passwordChangeRequestMock = jest.fn();
        userLogoutRequestMock = jest.fn();
        navigateMock = jest.fn();

        const { useUserActions } = require('@hooks/user/useUserActions');
        useUserActions.mockReturnValue({
            passwordChangeRequest: passwordChangeRequestMock,
        });

        const { useAuthActions } = require('@hooks/user/useAuthActions');
        useAuthActions.mockReturnValue({
            userLogoutRequest: userLogoutRequestMock,
        });

        const { useNavigation } = require('@contexts/NavigationContext');
        useNavigation.mockReturnValue(navigateMock);
    });

    afterEach(() => {
        jest.clearAllMocks();
    });

    it('should renders the form with title and input fields', () => {
        render(<PasswordChange />);

        expect(screen.getByText('app.user.change.password')).toBeInTheDocument();

        expect(screen.getByText('app.old.password')).toBeInTheDocument();
        expect(screen.getByText('app.new.password')).toBeInTheDocument();
        expect(screen.getByText('app.registr.form.pass.conf')).toBeInTheDocument();

        expect(screen.getByRole('button', { name: /app.registr.form.reg.buttom/i })).toBeInTheDocument();
    });

    it('should shows validation errors when inputs are invalid', async () => {
        render(<PasswordChange />);

        const oldInput = screen.getByLabelText('app.old.password');
        const newInput = screen.getByLabelText('app.new.password');
        const confirmInput = screen.getByLabelText('app.registr.form.pass.conf');
        const submitButton = screen.getByRole('button', { name: /app.registr.form.reg.buttom/i });

        fireEvent.change(oldInput, { target: { value: '' } });
        fireEvent.blur(oldInput);

        fireEvent.change(newInput, { target: { value: 'NewValid123!' } });
        fireEvent.blur(newInput);

        fireEvent.change(confirmInput, { target: { value: 'Mismatch123!' } });
        fireEvent.blur(confirmInput);

        await waitFor(() => {
            expect(screen.getByText('app.registr.form.error.not.match.pass.conf')).toBeInTheDocument();
        });

        expect(submitButton).toBeDisabled();
    });

    it('should submits valid data and calls passwordChangeRequest, then executes logout flow', async () => {
        const originalYupObject = Yup.object;
        
        Yup.object = () => ({
            shape: () => ({
                validate: jest.fn(() =>
                    Promise.resolve({
                        oldPassword: 'OldValid123!',
                        newPassword: 'NewValid123!',
                        confirmPassword: 'NewValid123!'
                    })
                ),
                validateAt: jest.fn(() => Promise.resolve())
            })
        });

        try {
            render(<PasswordChange />);

            const oldInput = screen.getByLabelText('app.old.password');
            const newInput = screen.getByLabelText('app.new.password');
            const confirmInput = screen.getByLabelText('app.registr.form.pass.conf');
            const submitButton = screen.getByRole('button', { name: /app.registr.form.reg.buttom/i });

            fireEvent.change(oldInput, { target: { value: 'OldValid123!' } });
            fireEvent.change(newInput, { target: { value: 'NewValid123!' } });
            fireEvent.change(confirmInput, { target: { value: 'NewValid123!' } });

            await waitFor(() => {
                expect(submitButton).not.toBeDisabled();
            });

            fireEvent.click(submitButton);

            await waitFor(() => {
                expect(passwordChangeRequestMock).toHaveBeenCalled();
            });

            const callArgs = passwordChangeRequestMock.mock.calls[0];
            expect(callArgs[0]).toBe('OldValid123!');
            expect(callArgs[1]).toBe('NewValid123!');
            expect(typeof callArgs[2]).toBe('function'); // setErrorMessage callback
            expect(typeof callArgs[3]).toBe('function'); // logout callback

            const logoutCall = callArgs[3];
            await logoutCall();

            expect(userLogoutRequestMock).toHaveBeenCalled();
        } finally {
            Yup.object = originalYupObject;
        }
    });

    it('should displays error message when submission fails validation', async () => {
        render(<PasswordChange />);

        const oldInput = screen.getByLabelText('app.old.password');
        const newInput = screen.getByLabelText('app.new.password');
        const confirmInput = screen.getByLabelText('app.registr.form.pass.conf');
        const submitButton = screen.getByRole('button', { name: /app.registr.form.reg.buttom/i });

        fireEvent.change(oldInput, { target: { value: 'OldValid123!' } });
        fireEvent.blur(oldInput);

        fireEvent.change(newInput, { target: { value: 'NewValid123!' } });
        fireEvent.blur(newInput);

        fireEvent.change(confirmInput, { target: { value: 'DifferentPassword!' } });
        fireEvent.blur(confirmInput);

        fireEvent.click(submitButton);

        await waitFor(() => {
            const errorElements = screen.getAllByText('app.registr.form.error.not.match.pass.conf');
            expect(errorElements.length).toBeGreaterThan(0);
        });
    });
});
