package org.osiam.test.integration

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.GreenMailUtil
import com.icegreen.greenmail.util.ServerSetupTest
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.ExtensionFieldType
import org.osiam.resources.scim.User

import javax.mail.Message

import static groovyx.net.http.ContentType.URLENC

/**
 * Integration test for lost password controller
 * @author Jochen Todea
 */
class LostPasswordIT extends AbstractIT {

    def mailServer

    def setup() {
        setupDatabase("database_seed_lost_password.xml")
        mailServer = new GreenMail(ServerSetupTest.ALL)
        mailServer.start()
    }

    def cleanup() {
        mailServer.stop()
    }

    def "URI: /password/lost/{userId} with POST method for lost password flow activation"() {
        given:
        def urn = "urn:scim:schemas:osiam:1.0:Registration"
        def userId = "cef8452e-00a9-4cec-a086-d171374febef"
        def accessToken = osiamConnector.retrieveAccessToken()
        def statusCode

        when:
        def httpClient = new HTTPBuilder(REGISTRATION_ENDPOINT)

        httpClient.request(Method.POST) { req ->
            uri.path = REGISTRATION_ENDPOINT + "/password/lost/" + userId
            headers.'Authorization' = 'Bearer ' + accessToken.getToken()

            response.success = { resp ->
                statusCode = resp.statusLine.statusCode
            }

            response.failure = { resp ->
                statusCode = resp.statusLine.statusCode
            }
        }

        then:
        statusCode == 200
        User user = osiamConnector.getUser(userId, accessToken)
        Extension extension = user.getExtension(urn)
        extension.getField("oneTimePassword", ExtensionFieldType.STRING) != null

        //Waiting at least 5 seconds for an E-Mail but aborts instantly if one E-Mail was received
        mailServer.waitForIncomingEmail(5000, 1)
        Message[] messages = mailServer.getReceivedMessages();
        messages.length == 1
        messages[0].getSubject() == "passwordLost"
        GreenMailUtil.getBody(messages[0]).contains("To reset your password, please click the link below:");
        messages[0].getFrom()[0].toString() == "noreply@osiam.org"
        messages[0].getAllRecipients()[0].toString().equals("george.alexander@osiam.org")
    }

    def "URI: /password/change with POST method to change the old with the new password and validating the user"() {
        given:
        def urn = "urn:scim:schemas:osiam:1.0:Registration"
        def accessToken = osiamConnector.retrieveAccessToken()
        def otp = "cef9452e-00a9-4cec-a086-a171374febef"
        def userId = "cef9452e-00a9-4cec-a086-d171374febef"
        def newPassword = "pulverToastMann"
        def statusCode
        def savedUserId

        when:
        def httpClient = new HTTPBuilder(REGISTRATION_ENDPOINT)

        httpClient.request(Method.POST) {
            uri.path = REGISTRATION_ENDPOINT + "/password/change"
            send URLENC, [oneTimePassword : otp, userId : userId, newPassword : newPassword]
            headers.'Authorization' = 'Bearer ' + accessToken.getToken()

            response.success = { resp, json ->
                statusCode = resp.statusLine.statusCode
                savedUserId = json.id
            }

            response.failure = { resp ->
                statusCode = resp.statusLine.statusCode
            }
        }

        then:
        statusCode == 200
        savedUserId == userId
        User user = osiamConnector.getUser(userId, accessToken)
        Extension extension = user.getExtension(urn)
        extension.getField("oneTimePassword", ExtensionFieldType.STRING) == ""
    }

    def "URI: /password/lostForm with GET method to get an html form with input field for the new password including known values as otp and userId"() {
        given:
        def otp = "otpVal"
        def userId = "userIdVal"

        def statusCode
        def responseContentType
        def responseContent

        when:
        def httpClient = new HTTPBuilder(REGISTRATION_ENDPOINT)

        httpClient.request(Method.GET, ContentType.TEXT) {
            uri.path = REGISTRATION_ENDPOINT + "/password/lostForm"
            uri.query = [oneTimePassword : otp, userId : userId]
            headers.Accept = "text/html"

            response.success = {resp, html ->
                statusCode = resp.statusLine.statusCode
                responseContentType = resp.headers.'Content-Type'
                responseContent = html.text
            }

            response.failure = { resp ->
                statusCode = resp.statusLine.statusCode
            }
        }

        then:
        statusCode == 200
        responseContentType == ContentType.HTML.toString()
        responseContent.contains('\$scope.otp = \'otpVal\'')
        responseContent.contains('\$scope.id = \'userIdVal\'')
        responseContent.count("ng-model") == 2
        responseContent.contains('url: \'http://test\'')
    }
}