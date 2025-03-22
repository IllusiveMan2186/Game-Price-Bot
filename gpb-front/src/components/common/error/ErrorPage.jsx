import React from "react";
import { useLocation } from "react-router-dom";
import Message from "@util/message";

export default function ErrorPage() {
    const location = useLocation();
    const errorMessage = location.state?.errorMessage || "app.gpb.default.error";

    return <Message string={errorMessage} />;
}
