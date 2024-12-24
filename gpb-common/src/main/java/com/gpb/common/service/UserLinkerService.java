package com.gpb.common.service;

/**
 * Handle user accounts integration with other accounts
 */
public interface UserLinkerService {

    /**
     * Link current user account with other account
     *
     * @param token              Connection token for linking
     * @param currentUserBasicId current user basic ID
     * @return new basic user id
     */
    Long linkAccounts(String token, long currentUserBasicId);

    /**
     * Generate a token for linking accounts
     *
     * @param basicUserId Web user ID
     * @return Account linking token
     */
    String getAccountsLinkerToken(long basicUserId);
}

