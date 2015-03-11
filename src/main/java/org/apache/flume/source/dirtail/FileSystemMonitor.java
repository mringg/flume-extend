package org.apache.flume.source.dirtail;

import java.io.IOException;
import java.nio.file.Path;

public class FileSystemMonitor {

    private FileMonitor fileMonitor;

    public FileSystemMonitor(final DirTailSource source, final DirPattern dirPattern) throws IOException {
        this.fileMonitor = new FileMonitor(dirPattern.getPath(), new FileMonitor.FileListener() {
            @Override
            public void fileCreate(Path path) {
                addJob(path, dirPattern, source, true);
            }

            @Override
            public void fileDelete(Path path) {
                source.removeTask(dirPattern.getPath() + "/" + path.getFileName().toString());
            }

            @Override
            public void fileModify(Path path) {
                addJob(path, dirPattern, source, false);
            }

        });
        fileMonitor.start();
    }

    public void stop() {
        this.fileMonitor.stop();
    }

    public void addJob(Path path, DirPattern dirPattern, DirTailSource source, boolean isNew) {
        String p = dirPattern.getPath() + "/" + path.getFileName().toString();
        if (!source.containTask(p) && dirPattern.isMatchFile(path)) {
            source.commitTask(p, path.getFileName().toString(), isNew);
        }
    }

}
