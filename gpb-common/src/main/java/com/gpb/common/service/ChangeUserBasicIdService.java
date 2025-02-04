package com.gpb.common.service;

/**
 * Service interface for handling updates to a user's basic identifier.
 * <p>
 * Implementations of this interface are responsible for updating the basic user ID
 * in the underlying data store. This functionality is essential for the proper operation
 * of the {@code ChangeBasicUserIdListener}, which triggers updates when a user's basic ID changes.
 * </p>
 */
public interface ChangeUserBasicIdService {

    /**
     * Updates the basic user ID for a user.
     *
     * @param currentBasicUserId the current basic user ID.
     * @param newBasicUserId     the new basic user ID to be set.
     */
    void setBasicUserId(long currentBasicUserId, long newBasicUserId);
}
