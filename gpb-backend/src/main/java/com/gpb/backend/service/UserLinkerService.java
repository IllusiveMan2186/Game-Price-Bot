package com.gpb.backend.service;

/**
 * Handle user accounts integration with other accounts
 */
public interface UserLinkerService {

    /**
     * Link external accounts (e.g., Telegram) to a web user
     *
     * @param token          Connection token
     * @param webBasicUserId Web user basic ID
     * @return new basic user id
     */
    Long linkAccounts(String token, long webBasicUserId);

    /**
     * Generate a token for linking accounts
     *
     * @param webUserId Web user ID
     * @return Account linking token
     */
    String getAccountsLinkerToken(long webUserId);
}

