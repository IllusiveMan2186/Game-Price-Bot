package com.gpb.game.service;

import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.BasicUser;

import java.util.List;

/**
 * Service interface for managing notifications .
 * <p>
 * This interface defines methods for sending notifications to users based on their preferences
 * </p>
 */
public interface NotificationManager {

    /**
     * Sends a notification to the specified user about changes in game information.
     * <p>
     * The notification is delivered according to the user's selected notification preferences and includes details
     * of the games whose information has changed.
     * </p>
     *
     * @param user           the user to be notified about the game information changes.
     * @param gameInShopList the list of {@link GameInShop} instances representing the games with updated information.
     */
    void sendGameInfoChange(BasicUser user, List<GameInShop> gameInShopList);
}
