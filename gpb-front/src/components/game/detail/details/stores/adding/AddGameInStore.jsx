import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import Message from '@util/message';

import { addGameInStoreByUrlRequest, } from '@services/gameRequests';

import addIcon from '@assets/images/add.png';

import './AddGameInStore.css';

const AddGameInStore = ({ gameId }) => {
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [url, setUrl] = useState('');
    const [isSent, setIsSent] = useState(false);
    const { t } = useTranslation();

    const handleIconClick = () => {
        setIsFormOpen(true);
    };

    const handleSend = async () => {
        if (!url) return; // Optionally, validate the URL

        try {
            await addGameInStoreByUrlRequest(gameId, url);
            setIsSent(true);
            setIsFormOpen(false);
            setUrl('');
        } catch (error) {
            console.error("Error adding game in store:", error);
            // Optionally, show an error message to the user.
        }
    };

    const placeholderText = t("app.game.add.placeholder");
    return (
        <div className="app-game__add-store">
            {isSent ? (
                // After sending, show the confirmation message.
                <Message string="app.game.add.send.message" />
            ) : (
                <>
                    {!isFormOpen ? (
                        // Show the add icon button when the form is closed.
                        <button type="button" onClick={handleIconClick} className="btn btn-link">
                            <img src={addIcon} alt="Add Game in Store" className="app-add-icon" />
                        </button>
                    ) : (
                        // When the form is open, show the input field and send button.
                        <div className="add-store-form">
                            <input
                                type="text"
                                placeholder={placeholderText}
                                value={url}
                                onChange={(e) => setUrl(e.target.value)}
                                className="form-control mb-2"
                            />
                            <button type="button" onClick={handleSend} className="btn btn-primary btn-block mb-4">
                                <Message string="app.game.add.button" />
                            </button>
                        </div>
                    )}
                </>
            )}
        </div>
    );
};

export default AddGameInStore;