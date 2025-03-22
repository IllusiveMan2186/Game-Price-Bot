import React from "react";
import { render, screen } from "@testing-library/react";
import ErrorPage from "./ErrorPage";

jest.mock("react-router-dom", () => ({
  useLocation: jest.fn(),
}));

jest.mock("@util/message", () => ({
  __esModule: true,
  default: ({ string }) => <span>{string}</span>,
}));

describe('ErrorPage Component', () => {
  it('should display provided error message from location state', () => {
    const mockUseLocation = require("react-router-dom").useLocation;
    mockUseLocation.mockReturnValue({
      state: { errorMessage: "app.custom.error" },
    });

    render(<ErrorPage />);
    expect(screen.getByText("app.custom.error")).toBeInTheDocument();
  });

  it('should display default error message if no error provided', () => {
    const mockUseLocation = require("react-router-dom").useLocation;
    mockUseLocation.mockReturnValue({});

    render(<ErrorPage />);
    expect(screen.getByText("app.gpb.default.error")).toBeInTheDocument();
  });
});
