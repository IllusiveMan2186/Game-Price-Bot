import React from "react";
import { useSearchParams } from "react-router-dom";
import { useEmailActions } from '@hooks/user/useEmailActions';

export default function EmailChangeConfirm() {
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");
    const { emailConfirmRequest } = useEmailActions();
    emailConfirmRequest(token);
}
