package org.osiam.client;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.client.query.Query;
import org.osiam.client.query.QueryBuilder;
import org.osiam.client.user.BasicUser;
import org.osiam.resources.scim.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/context.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
@DatabaseSetup("/database_seed_minimal.xml")
@DatabaseTearDown(value = "/database_tear_down.xml", type = DatabaseOperation.DELETE_ALL)
public class ParallelRequestsIT extends AbstractIntegrationTestBase {

    public static final int PARALLEL_REQUESTS = 1000;

    @Before
    public void addTestUsers() {
        accessToken = oConnector.retrieveAccessToken("marissa", "koala", Scope.ALL);
        for (int i = 0; i < PARALLEL_REQUESTS; i++) {
            User user = new User.Builder("user" + i)
                    .setPassword("user" + i)
                    .setActive(true)
                    .build();
            oConnector.createUser(user, accessToken);
        }
    }

    @Test
    public void concurrent_retrieval_of_access_tokens() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(PARALLEL_REQUESTS);
        final AtomicInteger failedTokens = new AtomicInteger();

        for (int i = 0; i < PARALLEL_REQUESTS; i++) {
            final int j = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Query userQuery = new QueryBuilder()
                                .filter("userName eq \"" + "user" + j + "\"")
                                .build();
                        BasicUser user = oConnector.getCurrentUserBasic(getUserToken(j));
                        oConnector.searchUsers(userQuery, getClientToken());
                        oConnector.searchUsers(userQuery, getClientToken());
                        oConnector.searchUsers(userQuery, getClientToken());
                        Query groupQuery = new QueryBuilder()
                                .count(2147483647)
                                .build();
                        oConnector.searchGroups(groupQuery, getClientToken());
                        oConnector.searchUsers(userQuery, getClientToken());
                        oConnector.getCurrentUserBasic(getUserToken(j));
                        oConnector.getUser(user.getId(), getClientToken());
                        oConnector.searchUsers(userQuery, getClientToken());
                        oConnector.getCurrentUserBasic(getUserToken(j));
                        oConnector.getUser(user.getId(), getClientToken());
                        oConnector.getCurrentUserBasic(getUserToken(j));
                        oConnector.getUser(user.getId(), getClientToken());
                    } catch (Exception e) {
                        failedTokens.incrementAndGet();
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            }).start();
        }

        latch.await();

        assertThat(failedTokens.get(), is(equalTo(0)));
    }

    private void assertValidAccessToken(AccessToken accessToken) {
        assertThat("Access token is empty or null: " + accessToken, accessToken.getToken(), not(isEmptyOrNullString()));
    }

    private AccessToken getClientToken() {
        AccessToken accessToken = oConnector.retrieveAccessToken(Scope.ALL);
        assertValidAccessToken(accessToken);
        return accessToken;
    }

    private AccessToken getUserToken(int index) {
        String userName = "user" + index;
        AccessToken accessToken = oConnector.retrieveAccessToken(userName, userName, Scope.ALL);
        assertValidAccessToken(accessToken);
        return accessToken;
    }

}
