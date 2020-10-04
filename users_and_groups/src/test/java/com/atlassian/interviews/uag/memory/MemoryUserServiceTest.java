package com.atlassian.interviews.uag.memory;

import com.atlassian.interviews.uag.api.User;
import com.atlassian.interviews.uag.api.UserService;
import com.atlassian.interviews.uag.core.ServiceFactory;
import com.atlassian.interviews.uag.core.Services;
import com.atlassian.interviews.uag.memory.MemoryUserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MemoryUserServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UserService userService;

    @Before
    public void setUp() {
        final Services services = ServiceFactory.createServices();
        userService = services.getUserService();
    }

    @Test
    public void testCreateUser_duplicate() {
        final User omar = new User("omar");
        userService.create(omar);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("User omar already exists");
        userService.create(omar);
    }


    @Test
    public void testCreateUser_npe() {
        thrown.expect(NullPointerException.class);
        userService.create(null);
    }

    @Test
    public void testCreateUser_ok() {
        assertNull("omar should not exist yet", userService.findByName("omar"));

        final User omar = new User("omar");
        userService.create(omar);

        assertEquals("omar should exist now", omar, userService.findByName("omar"));
    }

    @Test
    public void testDeleteUser_notExists() {
        assertNull("omar should not exist yet", userService.findByName("omar"));

        final User omar = new User("omar");
        userService.delete(omar);

        assertNull("omar still should not exist", userService.findByName("omar"));
    }

    @Test
    public void testDeleteUser_npe() {
        thrown.expect(NullPointerException.class);
        userService.delete(null);
    }

    @Test
    public void testDeleteUser_ok() {
        final User omar = new User("omar");

        userService.create(omar);
        assertEquals("omar should exist", omar, userService.findByName("omar"));

        userService.delete(omar);
        assertNull("omar should be deleted", userService.findByName("omar"));
    }
}
