/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.client;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osiam.client.exception.ForbiddenException;
import org.osiam.client.exception.NoResultException;
import org.osiam.client.oauth.Scope;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryBuilder;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.UpdateGroup;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/context.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DatabaseSetup("/database_seed_scope.xml")
@DatabaseTearDown(value = "/database_tear_down.xml", type = DatabaseOperation.DELETE_ALL)
public class ScopeIT extends AbstractIntegrationTestBase {

    private static final String VALID_USER_ID = "834b410a-943b-4c80-817a-4465aed037bc";
    private static final String VALID_GROUP_ID = "69e1a5dc-89be-4343-976c-b5541af249f4";

    @Test(expected = ForbiddenException.class)
    public void getting_user_in_DELETE_scope_raises_exception() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.DELETE);
        retrieveUser();
        fail("Exception expected");
    }

    @Test(expected = ForbiddenException.class)
    public void getting_group_in_DELETE_scope_raises_exception() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.DELETE);
        retrieveGroup();
        fail("Exception expected");
    }

    @Test(expected = ForbiddenException.class)
    public void getting_all_users_in_DELETE_scope_raises_exception() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.DELETE);
        getAllUsers();
        fail("Exception expected");
    }

    @Test(expected = ForbiddenException.class)
    public void getting_all_groups_in_DELETE_scope_raises_exception() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.DELETE);
        getAllGroups();
        fail("Exception expected");
    }

    @Test(expected = ForbiddenException.class)
    public void creating_a_user_in_DELETE_scope_raises_exception() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.DELETE);
        createUser();
        fail("Exception expected");
    }

    @Test(expected = ForbiddenException.class)
    public void creating_a_group_in_DELETE_scope_raises_exception() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.DELETE);
        createGroup();
        fail("Exception expected");
    }

    @Test(expected = ForbiddenException.class)
    public void getting_current_user_in_DELETE_scope_raises_exception() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.DELETE);
        getCurrentUser();
        fail("Exception expected");
    }

    @Test(expected = ForbiddenException.class)
    public void updating_user_in_DELETE_scope_raises_exception() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.DELETE);
        updateUser();
        fail("Exception expected");
    }

    @Test(expected = ForbiddenException.class)
    public void updating_group_in_DELETE_scope_raises_exception() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.DELETE);
        updateGroup();
        fail("Exception expected");
    }

    @Test(expected = ForbiddenException.class)
    public void searching_for_user_in_DELETE_scope_raises_exception() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.DELETE);
        searchForUsers();
        fail("Exception expected");
    }

    @Test(expected = ForbiddenException.class)
    public void searching_for_group_in_DELETE_scope_raises_exception() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.DELETE);
        searchForGroups();
        fail("Exception expected");
    }

    @Test(expected = ForbiddenException.class)
    public void deleting_user_in_GET_scope_raises_exception() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.GET);
        OSIAM_CONNECTOR.deleteUser(VALID_USER_ID, accessToken);
        fail("Exception expected");
    }

    @Test(expected = ForbiddenException.class)
    public void deleting_group_in_GET_scope_raises_exception() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.GET);
        OSIAM_CONNECTOR.deleteGroup(VALID_GROUP_ID, accessToken);
        fail("Exception expected");
    }

    @Test
    public void get_user_in_GET_scope_works() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.GET);
        assertThat(retrieveUser(), is(notNullValue()));
    }

    @Test
    public void get_group_in_GET_scope_works() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.GET);
        assertThat(retrieveGroup(), is(notNullValue()));
    }

    @Test
    public void get_all_users_in_GET_scope_works() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.GET);
        assertThat(getAllUsers(), is(notNullValue()));
    }

    @Test
    public void get_all_groups_in_GET_scope_works() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.GET);
        assertThat(getAllGroups(), is(notNullValue()));
    }

    @Test
    public void get_current_user_in_GET_scope_works() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.GET);
        assertThat(getCurrentUser(), is(notNullValue()));
    }

    @Test
    public void create_user_in_POST_scope_works() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.POST);
        assertThat(createUser(), is(notNullValue()));
    }

    @Test
    public void create_group_in_POST_scope_works() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.POST);
        assertThat(createGroup(), is(notNullValue()));
    }

    @Test
    @Ignore("Does not work anymore with new scopes. Method-based scopes will be removed soon!")
    public void update_user_in_PATCH_scope_works() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.PATCH);
        assertThat(updateUser(), is(notNullValue()));
    }

    @Test
    public void update_group_in_ALL_scope_works() {
        retrieveAccessTokenForMarissa();
        assertThat(updateGroup(), is(notNullValue()));
    }

    @Test
    public void search_for_users_in_GET_scope_works() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.GET);
        assertThat(searchForUsers(), is(notNullValue()));
    }

    @Test
    public void search_for_groups_in_GET_scope_works() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.GET);
        assertThat(searchForGroups(), is(notNullValue()));
    }

    @Test
    @Ignore("Does not work anymore with new scopes. Method-based scopes will be removed soon!")
    @ExpectedDatabase(value = "/database_expected_scope_delete_user.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void delete_user_in_DELETE_scope_works() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.DELETE);
        OSIAM_CONNECTOR.deleteUser(VALID_USER_ID, accessToken);
    }

    @Test(expected = NoResultException.class)
    public void delete_group_in_DELETE_scope_works() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.DELETE);

        OSIAM_CONNECTOR.deleteGroup(VALID_GROUP_ID, accessToken);

        OSIAM_CONNECTOR
                .getGroup(VALID_GROUP_ID,
                        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.GET));

        fail("Exception expected");
    }

    @Test
    public void different_scopes_different_token() {
        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.POST);

        String postScopesToken = accessToken.getToken();

        accessToken = OSIAM_CONNECTOR.retrieveAccessToken("marissa", "koala", Scope.GET);

        assertFalse(accessToken.getToken().equals(postScopesToken));
    }

    private User createUser() {
        User user = new User.Builder("userName").build();
        return OSIAM_CONNECTOR.createUser(user, accessToken);
    }

    private Group createGroup() {
        Group group = new Group.Builder("displayName").build();
        return OSIAM_CONNECTOR.createGroup(group, accessToken);
    }

    private User retrieveUser() {
        return OSIAM_CONNECTOR.getUser(VALID_USER_ID, accessToken);
    }

    private Group retrieveGroup() {
        return OSIAM_CONNECTOR.getGroup(VALID_GROUP_ID, accessToken);
    }

    private List<User> getAllUsers() {
        return OSIAM_CONNECTOR.getAllUsers(accessToken);
    }

    private List<Group> getAllGroups() {
        return OSIAM_CONNECTOR.getAllGroups(accessToken);
    }

    private User getCurrentUser() {
        return OSIAM_CONNECTOR.getCurrentUser(accessToken);
    }

    private User updateUser() {
        UpdateUser updateUser = new UpdateUser.Builder().updateUserName("newUserName").updateActive(false).build();
        return OSIAM_CONNECTOR.updateUser(VALID_USER_ID, updateUser, accessToken);
    }

    private Group updateGroup() {
        UpdateGroup updateGroup = new UpdateGroup.Builder().updateDisplayName("irrelevant").build();
        return OSIAM_CONNECTOR.updateGroup(VALID_GROUP_ID, updateGroup, accessToken);
    }

    private SCIMSearchResult<User> searchForUsers() {
        Query query = new QueryBuilder().startIndex(1).build();
        return OSIAM_CONNECTOR.searchUsers(query, accessToken);
    }

    private SCIMSearchResult<Group> searchForGroups() {
        Query query = new QueryBuilder().startIndex(1).build();
        return OSIAM_CONNECTOR.searchGroups(query, accessToken);
    }

}
