package org.filippenkov.certification_client.store;

import org.filippenkov.certification_client.models.User;

public final class UserProvider {
    private User user;
    private final static UserProvider INSTANCE = new UserProvider();

    private UserProvider() {}

    public static UserProvider getInstance() {
        return INSTANCE;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}
