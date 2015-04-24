package cc.blynk.server.utils;

import cc.blynk.server.model.DashBoard;
import cc.blynk.server.model.UserProfile;
import cc.blynk.server.model.widgets.Widget;
import cc.blynk.server.model.widgets.controls.Button;
import cc.blynk.server.model.widgets.others.Timer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.io.InputStream;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * User: ddumanskiy
 * Date: 21.11.13
 * Time: 13:27
 */
public class JsonParsingTest {

    //TODO Tests for all widget types!!!

    @Test
    public void testParseUserProfile() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json.txt");

        UserProfile userProfile = JsonParser.parseProfile(is);
        assertNotNull(userProfile);
        assertNotNull(userProfile.getDashBoards());
        assertEquals(userProfile.getDashBoards().length, 1);

        //this property shoudn't be parsed
        assertNull(userProfile.getActiveDashId());

        DashBoard dashBoard = userProfile.getDashBoards()[0];

        assertNotNull(dashBoard);

        assertEquals(1, dashBoard.getId());
        assertEquals("My Dashboard", dashBoard.getName());
        assertNotNull(dashBoard.getWidgets());
        assertEquals(dashBoard.getWidgets().length, 8);
        assertNotNull(dashBoard.getBoardType());
        assertEquals("UNO", dashBoard.getBoardType());

        for (Widget widget : dashBoard.getWidgets()) {
            assertNotNull(widget);
            assertEquals(1, widget.x);
            assertEquals(1, widget.y);
            assertEquals(1, widget.id);
            assertEquals("Some Text", widget.label);
        }
    }

    @Test
    public void testUserProfileToJson() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json.txt");

        UserProfile userProfile = JsonParser.parseProfile(is);
        String userProfileString = userProfile.toString();

        assertNotNull(userProfileString);
        assertTrue(userProfileString.contains("dashBoards"));
    }

    @Test
    public void testUserProfileToJson2() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json_2.txt");

        UserProfile userProfile = JsonParser.parseProfile(is);
        String userProfileString = userProfile.toString();

        assertNotNull(userProfileString);
        assertTrue(userProfileString.contains("dashBoards"));
    }

    @Test
    public void testUserProfileToJson3() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json_3.txt");

        UserProfile userProfile = JsonParser.parseProfile(is);
        String userProfileString = userProfile.toString();

        assertNotNull(userProfileString);
        assertTrue(userProfileString.contains("dashBoards"));
    }

    @Test
    public void testUserProfileToJsonWithTimer() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_with_timer.txt");

        UserProfile userProfile = JsonParser.parseProfile(is);
        String userProfileString = userProfile.toString();
        userProfile.setActiveDashId(1);

        assertNotNull(userProfileString);
        assertTrue(userProfileString.contains("dashBoards"));
        Set<Timer> timers = userProfile.getActiveDashboardTimerWidgets();
        assertNotNull(timers);
        assertEquals(1, timers.size());
    }

    @Test
    public void correctSerializedObject() throws JsonProcessingException {
        Button button = new Button();
        button.id = 1;
        button.label = "MyButton";
        button.x = 2;
        button.y = 2;
        button.pushMode = false;

        String result = JsonParser.mapper.writeValueAsString(button);

        assertEquals("{\"type\":\"BUTTON\",\"id\":1,\"x\":2,\"y\":2,\"label\":\"MyButton\",\"pushMode\":false}", result);
    }
}
