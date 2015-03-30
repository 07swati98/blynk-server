package cc.blynk.server.handlers.common;

import cc.blynk.common.model.messages.protocol.appllication.SaveProfileMessage;
import cc.blynk.server.TestBase;
import cc.blynk.server.exceptions.IllegalCommandException;
import cc.blynk.server.exceptions.NotAllowedException;
import cc.blynk.server.handlers.app.SaveProfileHandler;
import org.junit.Test;

import static cc.blynk.common.enums.Command.SAVE_PROFILE;
import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/26/2015.
 */
public class SaveProfileHandlerTest extends TestBase {

    private SaveProfileHandler saveProfileHandler = new SaveProfileHandler(props, null, null, null);

    @Test(expected = NotAllowedException.class)
    public void testTooBigUserProfile() throws Exception {
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < props.getIntProperty("user.profile.max.size") * 1024 + 1; i++) {
            tmp.append('a');
        }

        SaveProfileMessage msg = (SaveProfileMessage) produce(1, SAVE_PROFILE, tmp.toString());
        saveProfileHandler.messageReceived(null, null, msg);
    }

    @Test(expected = IllegalCommandException.class)
    public void testIllegalProfile() throws Exception {
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < props.getIntProperty("user.profile.max.size") * 1024; i++) {
            tmp.append('a');
        }

        SaveProfileMessage msg = (SaveProfileMessage) produce(1, SAVE_PROFILE, tmp.toString());
        saveProfileHandler.messageReceived(null, null, msg);
    }

}
