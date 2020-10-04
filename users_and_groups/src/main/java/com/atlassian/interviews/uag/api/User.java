package com.atlassian.interviews.uag.api;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

/**
 * A user that may or may not belong to any groups.
 */
@ParametersAreNonnullByDefault
public class User implements Comparable<User> {
    private final String name;

    /**
     * Creates a new instance of a user.
     * This does not implicitly register the user with the {@link UserService}.
     *
     * @param name the unique name that identifies this user; must not be {@code null}.
     */
    public User(String name) {
        this.name = requireNonNull(name, "name");
    }

    /**
     * Returns the name that identifies this user.
     *
     * @return the name that identifies this user.
     */
    public String getName() {
        return name;
    }

    public int compareTo(@Nonnull User other) {
        return name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
    
    
}
