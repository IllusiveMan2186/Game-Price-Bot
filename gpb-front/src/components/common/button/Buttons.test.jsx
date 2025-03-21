import { render, screen, fireEvent } from "@testing-library/react";
import { useAuth } from "@contexts/AuthContext";
import { useNavigation } from "@contexts/NavigationContext";
import Buttons from "./Buttons";

// Mock dependencies
jest.mock("@contexts/AuthContext", () => ({
    useAuth: jest.fn(),
}));

jest.mock("@contexts/NavigationContext", () => ({
    useNavigation: jest.fn(),
}));

jest.mock("@util/message", () => ({
    __esModule: true,
    default: function Message({ string }) {
        return <span>{string}</span>;
    }
}));

describe("Buttons Component", () => {
    let navigateMock;
    let isUserAuthMock;
    let logoutMock;

    beforeEach(() => {
        navigateMock = jest.fn();
        isUserAuthMock = jest.fn();
        logoutMock = jest.fn();

        useNavigation.mockReturnValue(navigateMock);
        useAuth.mockReturnValue({ isUserAuth: isUserAuthMock });
    });

    test("renders loading state when authentication is unknown", () => {
        isUserAuthMock.mockReturnValue(null);
        render(<Buttons logout={logoutMock} />);

        expect(screen.getByText("Loading...")).toBeInTheDocument();
    });

    test("renders login button when user is not authenticated", () => {
        isUserAuthMock.mockReturnValue(false);
        render(<Buttons logout={logoutMock} />);

        expect(screen.getByText("app.login")).toBeInTheDocument();
    });

    test("renders dropdown when user is authenticated", () => {
        isUserAuthMock.mockReturnValue(true);
        render(<Buttons logout={logoutMock} />);

        expect(screen.getByText("app.profile")).toBeInTheDocument();
    });

    test("calls navigate('/login') when login button is clicked", () => {
        isUserAuthMock.mockReturnValue(false);
        render(<Buttons logout={logoutMock} />);

        fireEvent.click(screen.getByText("app.login"));
        expect(navigateMock).toHaveBeenCalledWith("/login");
    });

    test("calls logout function when logout button is clicked", () => {
        isUserAuthMock.mockReturnValue(true);
        render(<Buttons logout={logoutMock} />);

        fireEvent.click(screen.getByText("app.profile.logout"));
        expect(logoutMock).toHaveBeenCalled();
    });
});
