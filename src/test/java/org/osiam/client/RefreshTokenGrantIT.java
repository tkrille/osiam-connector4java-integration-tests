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

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/context.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
@DatabaseSetup("/database_seed.xml")
@DatabaseTearDown(value = "/database_tear_down.xml", type = DatabaseOperation.DELETE_ALL)
public class RefreshTokenGrantIT {

    protected static final String AUTH_ENDPOINT_ADDRESS = "http://localhost:8180/osiam-auth-server";
    protected static final String RESOURCE_ENDPOINT_ADDRESS = "http://localhost:8180/osiam-resource-server";
    protected String clientId = "example-client";
    protected String clientSecret = "secret";

    @Test
    public void refreshing_an_access_token_should_provide_a_new_access_token(){

        //Get a access token.
        OsiamConnector connectorWithROPC = new OsiamConnector.Builder()
                .setAuthServerEndpoint(AUTH_ENDPOINT_ADDRESS)
                .setResourceServerEndpoint(RESOURCE_ENDPOINT_ADDRESS)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();

        AccessToken accessTokenROPC = connectorWithROPC.retrieveAccessToken("marissa", "koala", Scope.ALL);
        assertNotNull("The access token was null.", accessTokenROPC.getToken());
        assertNotNull("The refresh token was null.", accessTokenROPC.getRefreshToken());

        //Refresh the previously token with the retrieved refresh token
        AccessToken accessTokenRF = connectorWithROPC.refreshAccessToken(accessTokenROPC);

        assertNotNull("The access token after refresh was null.", accessTokenRF.getToken());
        assertNotNull("The refresh token after refresh was null.", accessTokenRF.getRefreshToken());

        //Check the the refresh token is equal and the access token is a new one
        assertEquals("The refresh tokens were not equal.", accessTokenROPC.getRefreshToken(), accessTokenRF.getRefreshToken());
        assertNotEquals("The access tokens were equal.", accessTokenROPC.getToken(), accessTokenRF.getToken());
    }
}
