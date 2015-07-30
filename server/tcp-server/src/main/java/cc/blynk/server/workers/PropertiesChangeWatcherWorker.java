package cc.blynk.server.workers;

import cc.blynk.common.utils.Config;
import cc.blynk.common.utils.ServerProperties;
import cc.blynk.server.handlers.BaseSimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/**
 * Hook that monitors server.properties file for modification.
 * In case of change - reloads "reloadable" properties in realtime.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/26/2015.
 */
public class PropertiesChangeWatcherWorker implements Runnable {

    private static final Logger log = LogManager.getLogger(PropertiesChangeWatcherWorker.class);

    private final String fileName;
    private final BaseSimpleChannelInboundHandler[] handlers;
    private final Path propsFileFolder;

    public PropertiesChangeWatcherWorker(String fileName, BaseSimpleChannelInboundHandler... handlers) {
        this.fileName = fileName;
        this.handlers = handlers;
        this.propsFileFolder = ServerProperties.getCurrentDir();
    }

    public PropertiesChangeWatcherWorker(List<BaseSimpleChannelInboundHandler> handlers) {
        this(Config.SERVER_PROPERTIES_FILENAME, handlers.toArray(new BaseSimpleChannelInboundHandler[handlers.size()]));
    }

    @Override
    public void run() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            propsFileFolder.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents()) {
                    Path changed = (Path) event.context();
                    Path changedFile = propsFileFolder.resolve(changed.toString());
                    if (changed.getFileName().toString().endsWith(fileName) && Files.exists(changedFile)) {
                        log.info("Props file '{}' changed. Updating handler properties.", changedFile);
                        ServerProperties changedProps = new ServerProperties(changed);
                        for (BaseSimpleChannelInboundHandler<?> handler : handlers) {
                            handler.updateProperties(changedProps);
                        }
                    }
                }
                // reset the key
                boolean valid = wk.reset();
                if (!valid) {
                    log.info("Key has been not unregistered.");
                }
            }
        } catch (IOException | InterruptedException e) {
            log.warn("Error monitoring '{}' file. Reloadable properties are not enabled.", Config.SERVER_PROPERTIES_FILENAME, e);
        }
    }
}
