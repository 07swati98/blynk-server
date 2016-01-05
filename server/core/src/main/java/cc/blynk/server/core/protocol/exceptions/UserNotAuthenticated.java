package cc.blynk.server.core.protocol.exceptions;

import cc.blynk.server.core.protocol.enums.Response;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/3/2015.
 */
public class UserNotAuthenticated extends BaseServerException {

    public UserNotAuthenticated(String message, int msgId) {
        super(message, msgId, Response.USER_NOT_AUTHENTICATED);
    }

}
