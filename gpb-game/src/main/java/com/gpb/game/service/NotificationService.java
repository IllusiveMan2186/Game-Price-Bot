package com.gpb.game.service;

import com.gpb.common.entity.game.GameInStoreDto;
import com.gpb.game.entity.user.BasicUser;

import java.util.List;

/**
 * Service interface responsible for sending notifications about game information changes
 * using a specific communication channel.
 * <p>
 * Implementations of this interface may use various mechanisms * to deliver updates to users when the information
 * of games they are interested in has changed.
 * </p>
 */
public interface NotificationService {

    /**
     * Sends a notification to the specified user containing information about changes in game details.
     * <p>
     * The notification includes details about the games whose information has been updated, represented by a
     * list of {@link GameInStoreDto} objects.
     * </p>
     *
     * @param user           the user to be notified about the game information changes.
     * @param gameInShopList a list of {@link GameInStoreDto} objects representing games with updated information.
     */
    void sendGameInfoChange(BasicUser user, List<GameInStoreDto> gameInShopList);
}
