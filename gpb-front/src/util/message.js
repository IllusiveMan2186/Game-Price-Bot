import { useTranslation } from "react-i18next";

export default function Message(props) {
    const { t } = useTranslation();

    return (t(props.string));
}