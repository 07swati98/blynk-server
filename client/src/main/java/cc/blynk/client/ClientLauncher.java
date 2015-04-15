package cc.blynk.client;

import cc.blynk.client.core.AppClient;
import cc.blynk.client.core.BaseClient;
import cc.blynk.client.core.HardwareClient;
import cc.blynk.client.enums.ClientMode;
import cc.blynk.common.model.messages.protocol.PingMessage;
import cc.blynk.common.utils.ParseUtil;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.03.15.
 */
public class ClientLauncher {

    protected static final String DEFAULT_HOST = "localhost";
    protected static final int DEFAULT_HARDWARE_PORT = 8442;
    protected static final int DEFAULT_APPLICATION_PORT = 8443;

    private static final Options options = new Options();

    static {
        options.addOption("host", true, "Server host or ip.")
               .addOption("port", true, "Port client should connect to.")
               .addOption("mode", true, "Client mode. 'hardware' or 'app'.")
               .addOption("disableAppSsl", false, "Disables SSL for app mode.");
    }

    public static void main(String[] args) throws ParseException {
        CommandLine cmd = new BasicParser().parse(options, args);

        ClientMode mode = ClientMode.parse(cmd.getOptionValue("mode", ClientMode.HARDWARE.name()));
        String host = cmd.getOptionValue("host", DEFAULT_HOST);
        int port = ParseUtil.parseInt(cmd.getOptionValue("port",
            (mode == ClientMode.APP ? String.valueOf(DEFAULT_APPLICATION_PORT) : String.valueOf(DEFAULT_HARDWARE_PORT)))
        );
        boolean disableAppSsl = cmd.hasOption("disableAppSsl");

        BaseClient baseClient = mode == ClientMode.APP ? new AppClient(host, port, disableAppSsl) : new HardwareClient(host, port);

        //pinging for hardware client to avoid closing from server side for inactivity
        if (mode == ClientMode.HARDWARE) {
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
                baseClient.send(new PingMessage(777, ""));
            }, 12, 12, TimeUnit.SECONDS);
        }

        baseClient.start(new BufferedReader(new InputStreamReader(System.in)));
    }

}
