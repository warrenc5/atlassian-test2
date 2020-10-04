package com.atlassian.interviews.uag.memory;

import com.atlassian.interviews.uag.api.Group;
import com.atlassian.interviews.uag.api.GroupService;
import com.atlassian.interviews.uag.api.MembershipService;
import com.atlassian.interviews.uag.api.User;
import com.atlassian.interviews.uag.core.ServiceFactory;
import com.atlassian.interviews.uag.core.Services;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MemoryGroupServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private GroupService groupService;
    private MembershipService membershipService;
    private User sven,wozza;
    
    @Before
    public void setUp() {
        final Services services = ServiceFactory.createServices();
        sven = new User("sven");
        wozza = new User("wozza");
        
        services.getUserService().create(sven);
        services.getUserService().create(wozza);
        
        groupService = services.getGroupService();
        membershipService = services.getMembershipService();
    }

    @Test
    public void testCreateGroup_duplicate() {
        final Group hackers = new Group("hackers");
        groupService.create(hackers);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Group hackers already exists");
        groupService.create(hackers);
    }


    @Test
    public void testCreateGroup_npe() {
        thrown.expect(NullPointerException.class);
        groupService.create(null);
    }

    @Test
    public void testCreateGroup_ok() {
        assertNull("hackers should not exist yet", groupService.findByName("hackers"));

        final Group hackers = new Group("hackers");
        groupService.create(hackers);

        assertEquals("hackers should exist now", hackers, groupService.findByName("hackers"));
    }

    @Test
    public void testDeleteGroup_notExists() {
        assertNull("hackers should not exist yet", groupService.findByName("hackers"));

        final Group hackers = new Group("hackers");
        groupService.delete(hackers);

        assertNull("hackers still should not exist", groupService.findByName("hackers"));
    }

    @Test
    public void testDeleteGroup_npe() {
        thrown.expect(NullPointerException.class);
        groupService.delete(null);
    }

    @Test
    public void testDeleteGroup_ok() {
        final Group hackers = new Group("hackers");

        groupService.create(hackers);
        assertEquals("hackers should exist", hackers, groupService.findByName("hackers"));

        groupService.delete(hackers);
        assertNull("hackers should be deleted", groupService.findByName("hackers"));
    }
    
    @Test
    public void testDeleteGroupAndUsers_ok() {
        final Group hackers = new Group("hackers");

        
        groupService.create(hackers);
        assertEquals("hackers should exist", hackers, groupService.findByName("hackers"));

        this.membershipService.addUserToGroup(sven, hackers);
        this.membershipService.addUserToGroup(wozza, hackers);

        groupService.delete(hackers);
        assertNull("hackers should be deleted", groupService.findByName("hackers"));
        
        groupService.create(hackers);
        
        assertTrue(this.membershipService.getUsersInGroup(hackers).isEmpty());
    }
}
