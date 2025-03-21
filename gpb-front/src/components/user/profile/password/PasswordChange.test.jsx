import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import PasswordChange from './PasswordChange';
import * as Yup from 'yup';

// Mock Message so that it simply renders the provided string.
jest.mock('@util/message', () => ({
    __esModule: true,
    default: ({ string }) => <span>{string}</span>
}));

// Mock hooks for user actions, auth actions, auth context, and navigation.
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
    let logoutMock;
    let navigateMock;

    beforeEach(() => {
        // Create fresh mocks for each test.
        passwordChangeRequestMock = jest.fn();
        userLogoutRequestMock = jest.fn();
        logoutMock = jest.fn();
        navigateMock = jest.fn();

        const { useUserActions } = require('@hooks/user/useUserActions');
        useUserActions.mockReturnValue({
            passwordChangeRequest: passwordChangeRequestMock,
        });

        const { useAuthActions } = require('@hooks/user/useAuthActions');
        useAuthActions.mockReturnValue({
            userLogoutRequest: userLogoutRequestMock,
        });

        const { useAuth } = require('@contexts/AuthContext');
        useAuth.mockReturnValue({
            logout: logoutMock,
        });

        const { useNavigation } = require('@contexts/NavigationContext');
        useNavigation.mockReturnValue(navigateMock);
    });

    afterEach(() => {
        jest.clearAllMocks();
    });

    it('should renders the form with title and input fields', () => {
        render(<PasswordChange />);

        // Check that the title is rendered.
        expect(screen.getByText('app.user.change.password')).toBeInTheDocument();

        // Check that the labels are rendered (via Message).
        expect(screen.getByText('app.old.password')).toBeInTheDocument();
        expect(screen.getByText('app.new.password')).toBeInTheDocument();
        expect(screen.getByText('app.registr.form.pass.conf')).toBeInTheDocument();

        // The submit button should be present.
        expect(screen.getByRole('button', { name: /app.registr.form.reg.buttom/i })).toBeInTheDocument();
    });

    it('should shows validation errors when inputs are invalid', async () => {
        render(<PasswordChange />);

        // Get input elements by their labels (labels render the Message content).
        const oldInput = screen.getByLabelText('app.old.password');
        const newInput = screen.getByLabelText('app.new.password');
        const confirmInput = screen.getByLabelText('app.registr.form.pass.conf');
        const submitButton = screen.getByRole('button', { name: /app.registr.form.reg.buttom/i });

        // Simulate invalid input:
        // Leave oldPassword empty and provide mismatching newPassword/confirmPassword.
        fireEvent.change(oldInput, { target: { value: '' } });
        fireEvent.blur(oldInput);

        fireEvent.change(newInput, { target: { value: 'NewValid123!' } });
        fireEvent.blur(newInput);

        fireEvent.change(confirmInput, { target: { value: 'Mismatch123!' } });
        fireEvent.blur(confirmInput);

        // Wait for Yup to validate and show the error for confirmPassword.
        await waitFor(() => {
            expect(screen.getByText('app.registr.form.error.not.match.pass.conf')).toBeInTheDocument();
        });

        // The submit button should remain disabled.
        expect(submitButton).toBeDisabled();
    });

    it('should submits valid data and calls passwordChangeRequest, then executes logout flow', async () => {
        // --- Override Yup's validation to always resolve ---
        const originalYupObject = Yup.object;
        // Override Yup.object() so that:
        // - shape() returns an object with validate() and validateAt() always resolving.
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
        // -----------------------------------------------------

        try {
            render(<PasswordChange />);

            // Get inputs by their labels (they render Message strings)
            const oldInput = screen.getByLabelText('app.old.password');
            const newInput = screen.getByLabelText('app.new.password');
            const confirmInput = screen.getByLabelText('app.registr.form.pass.conf');
            const submitButton = screen.getByRole('button', { name: /app.registr.form.reg.buttom/i });

            // Provide valid inputs.
            fireEvent.change(oldInput, { target: { value: 'OldValid123!' } });
            fireEvent.change(newInput, { target: { value: 'NewValid123!' } });
            fireEvent.change(confirmInput, { target: { value: 'NewValid123!' } });

            // We do not trigger onBlur here because our override makes validation always pass.
            // Wait until the submit button becomes enabled.
            await waitFor(() => {
                expect(submitButton).not.toBeDisabled();
            });

            // Submit the form.
            fireEvent.click(submitButton);

            // Wait for passwordChangeRequest to be called.
            await waitFor(() => {
                expect(passwordChangeRequestMock).toHaveBeenCalled();
            });

            // Verify that passwordChangeRequest was called with:
            //  - oldPassword, newPassword,
            //  - a callback for setting errorMessage,
            //  - and a logout callback.
            const callArgs = passwordChangeRequestMock.mock.calls[0];
            expect(callArgs[0]).toBe('OldValid123!');
            expect(callArgs[1]).toBe('NewValid123!');
            expect(typeof callArgs[2]).toBe('function'); // setErrorMessage callback
            expect(typeof callArgs[3]).toBe('function'); // logout callback

            // Simulate the logout flow by calling the logout callback.
            const logoutCall = callArgs[3];
            await logoutCall();

            // Verify that the logout functions and navigation were triggered.
            expect(logoutMock).toHaveBeenCalled();
            expect(userLogoutRequestMock).toHaveBeenCalled();
            expect(navigateMock).toHaveBeenCalledWith('/');
        } finally {
            // Restore the original Yup.object
            Yup.object = originalYupObject;
        }
    });

    it('should displays error message when submission fails validation', async () => {
        // In this test, provide valid old and new passwords but mismatching confirmation.
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

        // Submit the form.
        fireEvent.click(submitButton);

        // Use getAllByText since the error message might be rendered in more than one place.
        await waitFor(() => {
            const errorElements = screen.getAllByText('app.registr.form.error.not.match.pass.conf');
            expect(errorElements.length).toBeGreaterThan(0);
        });
    });
});
