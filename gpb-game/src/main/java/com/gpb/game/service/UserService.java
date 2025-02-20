package com.gpb.game.service;

import com.gpb.common.entity.user.UserNotificationType;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.BasicUser;

import java.util.List;

/**
 * Service interface for managing user operations.
 * <p>
 * This interface provides methods for retrieving, creating, and linking user accounts, as well as managing
 * game subscriptions for users.
 * </p>
 */
public interface UserService {

    /**
     * Retrieves the {@link BasicUser} identified by the specified user ID.
     *
     * @param userId the unique identifier of the user.
     * @return the {@link BasicUser} associated with the given ID.
     */
    BasicUser getUserById(long userId);

    /**
     * Creates a new basic user with the specified notification type.
     *
     * @param notificationType the {@link UserNotificationType} to be associated with the new user.
     * @return the newly created {@link BasicUser}.
     */
    BasicUser createUser(UserNotificationType notificationType);

    /**
     * Links two user accounts by merging information from the source account into another account.
     * <p>
     * The linking process uses a provided token to associate and merge user data.
     * </p>
     *
     * @param token        the linking token used to identify and authenticate the linking request.
     * @param sourceUserId the unique identifier of the source user whose account information is to be merged.
     * @return the updated {@link BasicUser} after linking the accounts.
     */
    BasicUser linkUsers(String token, long sourceUserId);

    /**
     * Generates an account linking token for the user identified by the given user ID.
     *
     * @param userId the unique identifier of the user for whom the token is generated.
     * @return a token as a {@code String} that can be used for linking accounts.
     */
    String createAccountLinkerToken(long userId);

    /**
     * Retrieves a list of users who are subscribed to games that have experienced information changes.
     *
     * @param changedGames a list of {@link GameInShop} instances representing games with updated information.
     * @return a list of {@link BasicUser} objects who are subscribed to the changed games.
     */
    List<BasicUser> getUsersOfChangedGameInfo(List<GameInShop> changedGames);

    /**
     * Subscribes the user identified by the given user ID to the game identified by the given game ID.
     * <p>
     * This subscription enables the user to receive updates about changes in the game information.
     * </p>
     *
     * @param userId the unique identifier of the user.
     * @param gameId the unique identifier of the game.
     */
    void subscribeToGame(long userId, long gameId);

    /**
     * Unsubscribes the user identified by the given user ID from the game identified by the given game ID.
     * <p>
     * This action removes the game from the user's subscription list, and the user will no longer receive updates for it.
     * </p>
     *
     * @param userId the unique identifier of the user.
     * @param gameId the unique identifier of the game.
     */
    void unsubscribeFromGame(long userId, long gameId);
}
