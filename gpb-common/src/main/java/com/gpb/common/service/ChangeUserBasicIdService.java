package com.gpb.common.service;

/**
 * Service interface for handling changes to a user's basic ID.
 * <p>
 * This interface must be implemented to enable the {@code ChangeBasicUserIdListener}
 * to function correctly within the service.
 */
public interface ChangeUserBasicIdService {

    /**
     * Set new basic user id
     *
     * @param currentBasicUserId current basic user id
     * @param newBasicUserId     new basic user id
     */
    void setBasicUserId(long currentBasicUserId, long newBasicUserId);
}
