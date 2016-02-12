/*
 *  [2012] - [2016] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.auth.sso.server;

import org.eclipse.che.api.auth.AuthenticationException;

import com.codenvy.api.dao.authentication.AccessTicket;
import com.codenvy.api.dao.authentication.CookieBuilder;
import com.codenvy.api.dao.authentication.TicketManager;
import com.codenvy.api.dao.authentication.TokenGenerator;
import com.codenvy.auth.sso.server.handler.BearerTokenAuthenticationHandler;
import com.codenvy.auth.sso.server.organization.UserCreator;
import com.codenvy.auth.sso.server.organization.UserCreationValidator;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.commons.lang.IoUtil;
import org.eclipse.che.commons.user.User;

import org.codenvy.mail.MailSenderClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Service to authenticate users using bearer tokens.
 * <p/>
 *
 * @author Alexander Garagatyi
 * @author Sergey Kabashniuk
 */
@Path("internal/token")
public class BearerTokenAuthenticationService {

    private static final Logger LOG           = LoggerFactory.getLogger(BearerTokenAuthenticationService.class);
    // TODO made this configurable
    private static final String MAIL_TEMPLATE = "email-templates/verify_email_address.html";
    @Inject
    protected TicketManager                    ticketManager;
    @Inject
    protected TokenGenerator                   uniqueTokenGenerator;
    @Inject
    private   BearerTokenAuthenticationHandler handler;
    @Inject
    protected MailSenderClient                 mailSenderClient;
    @Inject
    protected InputDataValidator               inputDataValidator;
    @Inject
    protected CookieBuilder                    cookieBuilder;
    @Inject
    protected UserCreationValidator            creationValidator;
    @Inject
    protected UserCreator                      userCreator;


    /**
     * Authenticates user by provided token, than creates the ldap user
     * and set the access/logged in cookies.
     *
     * @param credentials
     *         user credentials
     * @return principal user principal
     * @throws AuthenticationException
     */
    @POST
    @Path("authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(@CookieParam("session-access-key") Cookie tokenAccessCookie,
                                 Credentials credentials, @Context UriInfo uriInfo) throws AuthenticationException {

        if (handler == null) {
            LOG.warn("Bearer authenticator is null.");
            return Response.serverError().build();
        }

        boolean isSecure = uriInfo.getRequestUri().getScheme().equals("https");
        if (!handler.isValid(credentials.getToken())) {
            throw new AuthenticationException("Provided token is not valid");
        }
        Map<String, String> payload = handler.getPayload(credentials.getToken());
        handler.authenticate(credentials.getToken());
        final String username =  payload.get("username");
        try {
            User principal = userCreator.createUser(payload.get("email"), username, payload.get("firstName"), payload.get("lastName"));

            Response.ResponseBuilder builder = Response.ok();
            if (tokenAccessCookie != null) {
                AccessTicket accessTicket = ticketManager.getAccessTicket(tokenAccessCookie.getValue());
                if (accessTicket != null) {
                    if (!principal.equals(accessTicket.getPrincipal())) {
                        // DO NOT REMOVE! This log will be used in statistic analyzing
                        LOG.info("EVENT#user-changed-name# OLD-USER#{}# NEW-USER#{}#",
                                 accessTicket.getPrincipal().getName(),
                                 principal.getName());
                        LOG.info("EVENT#user-sso-logged-out# USER#{}#", accessTicket.getPrincipal().getName());
                        // DO NOT REMOVE! This log will be used in statistic analyzing
                        ticketManager.removeTicket(accessTicket.getAccessToken());
                    }
                } else {
                    //cookie is outdated, clearing
                    cookieBuilder.clearCookies(builder, tokenAccessCookie.getValue(), isSecure);
                }
            }

            if (payload.containsKey("initiator")) {
                // DO NOT REMOVE! This log will be used in statistic analyzing
                LOG.info("EVENT#user-sso-logged-in# USING#{}# USER#{}#", payload.get("initiator"), username);
            }


            // If we obtained principal  - authentication is done.
            String token = uniqueTokenGenerator.generate();
            ticketManager.putAccessTicket(new AccessTicket(token, principal, "bearer"));

            cookieBuilder.setCookies(builder, token, isSecure);
            builder.entity(Collections.singletonMap("token", token));

            LOG.debug("Authenticate user {} with token {}", username, token);
            return builder.build();
        } catch (IOException e) {
            throw new AuthenticationException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Validates user email and user name, than sends confirmation mail.
     *
     * @param validationData
     *         - email and user name for validation
     * @param uriInfo
     * @return
     * @throws java.io.IOException
     * @throws MessagingException
     */
    @POST
    @Path("validate")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validate(ValidationData validationData, @Context UriInfo uriInfo)
            throws ConflictException, MessagingException, ServerException, IOException {

        try {
            inputDataValidator.validateUserMail(validationData.getEmail());
            creationValidator.ensureUserCreationAllowed(validationData.getEmail(), validationData.getUsername());
        } catch (InputDataException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

        Map<String, String> props = new HashMap<>();
        props.put("com.codenvy.masterhost.url", uriInfo.getBaseUriBuilder().replacePath(null).build().toString());
        props.put("bearertoken", handler.generateBearerToken(validationData.getEmail(), validationData.getUsername(),
                                                             Collections.singletonMap("initiator", "email")));
        props.put("additional.query.params", uriInfo.getRequestUri().getQuery());

        mailSenderClient.sendMail("Codenvy <noreply@codenvy.com>", validationData.getEmail(), null,
                                  "Verify Your Codenvy Account",
                                  MediaType.TEXT_HTML,
                                  IoUtil.readAndCloseQuietly(IoUtil.getResource("/" + MAIL_TEMPLATE)), props);

        LOG.info("EVENT#signup-validation-email-send# EMAIL#{}#", validationData.getEmail());
        LOG.info("Email validation message send to {}", validationData.getEmail());

        return Response.ok().build();

    }

    public static class ValidationData {
        private String email;
        private String userName;

        public ValidationData() {
        }

        public ValidationData(String email, String userName) {
            this.email = email;
            this.userName = userName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUsername() {
            return userName;
        }

        public void setUsername(String userName) {
            this.userName = userName;
        }
    }

    public static class Credentials {
        private String token;

        public Credentials() {
        }

        public Credentials(String username, String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
