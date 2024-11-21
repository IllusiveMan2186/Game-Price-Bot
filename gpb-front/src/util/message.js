import { useTranslation } from "react-i18next";

export default function Message(props) {
    console.info(props.string + " " + window.localStorage.getItem('locale'))

    const { t } = useTranslation();

    return (t(props.string));
}