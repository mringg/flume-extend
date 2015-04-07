package org.apache.flume.source.dirtail;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class FileMonitor {

    private Thread           monitor;

    private volatile boolean running = true;

    private WatchService     watcher = FileSystems.getDefault().newWatchService();

    public FileMonitor(final String path, final FileListener l, final long delay) throws IOException {
        this.monitor = new Thread(new Runnable() {
            @Override
            public void run() {
                Path p = Paths.get(path);
                try {
                    p.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                } catch (IOException e) {
                }
                while (running) {
                    try {
                        Thread.sleep(delay);
                        WatchKey key = watcher.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            @SuppressWarnings("unchecked")
                            Kind<Path> kind = (Kind<Path>) event.kind();
                            if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                                l.fileModify((Path) event.context());
                            } else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                                l.fileCreate((Path) event.context());
                            } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                                l.fileDelete((Path) event.context());
                            }
                        }
                        key.reset();
                    } catch (Exception e) {
                    }
                }
                try {
                    watcher.close();
                } catch (IOException e) {
                }
            }
        });
    }

    public void stop() {
        running = false;
    }

    public void start() {
        monitor.start();
    }

    public interface FileListener {

        public void fileCreate(Path path);

        public void fileDelete(Path path);

        public void fileModify(Path path);

    }
}
