package com.atlassian.interviews.uag.memory;

import com.atlassian.interviews.uag.api.Group;
import com.atlassian.interviews.uag.api.GroupService;
import com.atlassian.interviews.uag.api.MembershipService;
import com.atlassian.interviews.uag.api.User;
import com.atlassian.interviews.uag.api.UserService;
import com.atlassian.interviews.uag.core.ServiceFactory;
import com.atlassian.interviews.uag.core.Services;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MemoryMembershipNestedGroupsTest {
    private static final User EVAN = new User("evan");
    private static final User OMAR = new User("omar");
    private static final User RITA = new User("rita");
    private static final User NOBODY = new User("nobody");
    private static final Group HACKERS = new Group("hackers");
    private static final Group ADMINS = new Group("admins");
    private static final Group PEOPLE = new Group("people");
    private static final Group NOGROUP = new Group("nogroup");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MembershipService membershipService;

    @Before
    public void setUp() {
        final Services services = ServiceFactory.createServices();

        final UserService userService = services.getUserService();
        userService.create(EVAN);
        userService.create(OMAR);
        userService.create(RITA);

        final GroupService groupService = services.getGroupService();
        groupService.create(ADMINS);
        groupService.create(HACKERS);
        groupService.create(PEOPLE);

        membershipService = services.getMembershipService();
        membershipService.addUserToGroup(EVAN, PEOPLE);
        membershipService.addUserToGroup(OMAR, ADMINS);
        membershipService.addUserToGroup(OMAR, PEOPLE);
        membershipService.addUserToGroup(RITA, HACKERS);
        membershipService.addGroupToGroup(ADMINS, PEOPLE);
        membershipService.addGroupToGroup(HACKERS, PEOPLE);
    }

    @Test
    public void addGroupToGroup_nullParent() {
        thrown.expect(NullPointerException.class);
        membershipService.addGroupToGroup(HACKERS, null);
    }

    @Test
    public void addGroupToGroup_nullChild() {
        thrown.expect(NullPointerException.class);
        membershipService.addGroupToGroup(null, PEOPLE);
    }

    @Ignore("TODO - Inheritance isn't working yet")
    @Test
    public void addGroupToGroup_duplicate() {
        membershipService.addGroupToGroup(HACKERS, PEOPLE);
        membershipService.addGroupToGroup(HACKERS, PEOPLE);
        assertTrue("rita is a hacker, and hackers are people", membershipService.isUserInGroup(RITA, PEOPLE));

        membershipService.removeGroupFromGroup(HACKERS, PEOPLE);
        assertFalse("one remove is good enough", membershipService.isUserInGroup(RITA, PEOPLE));
    }

    @Test
    public void testIsGroupInGroup_yes() {
        assertTrue("hackers are people", membershipService.isGroupInGroup(HACKERS, PEOPLE));
    }

    @Test
    public void testIsGroupInGroup_no() {
        assertFalse("hackers are not implicitly admins", membershipService.isGroupInGroup(HACKERS, ADMINS));
        assertFalse("people are not implicitly admins", membershipService.isGroupInGroup(PEOPLE, ADMINS));
        assertFalse("people are not implicitly hackers", membershipService.isGroupInGroup(PEOPLE, HACKERS));
    }

    @Test
    public void removeGroupFromGroup_nullParent() {
        thrown.expect(NullPointerException.class);
        membershipService.removeGroupFromGroup(HACKERS, null);
    }

    @Test
    public void removeGroupFromGroup_nullChild() {
        thrown.expect(NullPointerException.class);
        membershipService.removeGroupFromGroup(null, PEOPLE);
    }

    @Ignore("TODO - Inheritance isn't working yet")
    @Test
    public void removeUserFromGroup_indirect() {
        membershipService.removeUserFromGroup(RITA, PEOPLE);
        assertTrue("did not remove the indirect membership", membershipService.isUserInGroup(RITA, PEOPLE));
    }

    @Test
    public void removeGroupFromGroup_removed() {
        membershipService.removeGroupFromGroup(HACKERS, PEOPLE);
        assertFalse("hackers are no longer people", membershipService.isGroupInGroup(HACKERS, PEOPLE));
    }

    @Test
    public void removeGroupFromGroup_notMember() {
        membershipService.removeGroupFromGroup(HACKERS, ADMINS);
        membershipService.removeGroupFromGroup(HACKERS, ADMINS);
    }

    @Test
    public void testDirectMembershipsWork() {
        assertTrue("omar is an admin", membershipService.isUserInGroup(OMAR, ADMINS));
        assertTrue("rita is a hacker", membershipService.isUserInGroup(RITA, HACKERS));
        assertFalse("omar is not a hacker", membershipService.isUserInGroup(OMAR, HACKERS));
        assertFalse("rita is not an admin", membershipService.isUserInGroup(RITA, ADMINS));
    }

    @Ignore("TODO - Inheritance isn't working yet")
    @Test
    public void testInheritedMembership() {
        assertTrue("rita is a hacker, and hackers are people", membershipService.isUserInGroup(RITA, PEOPLE));
    }

    @Test
    public void testGetUsersInGroupDoesNotDoInheritance() {
        final Set<User> expectedPeople = new HashSet<>(asList(EVAN, OMAR));
        final Set<User> actualPeople = new HashSet<>(membershipService.getUsersInGroup(PEOPLE));
        assertEquals(expectedPeople, actualPeople);
    }
}
