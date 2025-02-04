package com.gpb.common.service;

/**
 * Service interface for linking user accounts with external accounts.
 * <p>
 * Implementations of this interface provide mechanisms to integrate a user's account with another account,
 * allowing for seamless account linking through secure token-based verification.
 * </p>
 */
public interface UserLinkerService {

    /**
     * Links the current user's account with an external account using the provided connection token.
     *
     * @param token              the connection token used to link the accounts.
     * @param currentUserBasicId the basic identifier of the current user.
     */
    void linkAccounts(String token, long currentUserBasicId);

    /**
     * Generates a token for linking a user's account with an external account.
     *
     * @param basicUserId the identifier of the user for whom the linking token is to be generated.
     * @return a token that can be used to link the user's account with an external account.
     */
    String getAccountsLinkerToken(long basicUserId);
}
