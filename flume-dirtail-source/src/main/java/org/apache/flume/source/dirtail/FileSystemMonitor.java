package org.apache.flume.source.dirtail;

import java.io.IOException;
import java.nio.file.Path;

public class FileSystemMonitor {

    private FileMonitor fileMonitor;

    public FileSystemMonitor(final DirTailSource source, final DirPattern dirPattern, final long delay) throws IOException {
        this.fileMonitor = new FileMonitor(dirPattern.getPath(), new FileMonitor.FileListener() {
            @Override
            public void fileCreate(Path path) {
                addJob(path, dirPattern, source, true);
            }

            @Override
            public void fileDelete(Path path) {
                source.removeTask(buildP(dirPattern, path));
            }

            @Override
            public void fileModify(Path path) {
                addJob(path, dirPattern, source, false);
            }

        }, delay);
        fileMonitor.start();
    }

    public void stop() {
        this.fileMonitor.stop();
    }

    public void addJob(Path path, DirPattern dirPattern, DirTailSource source, boolean isNew) {
        String p = buildP(dirPattern, path);
        if (!source.containTask(p) && dirPattern.isMatchFile(path)) {
            source.commitTask(p, path.getFileName().toString(), isNew);
        }
    }

    public String buildP(DirPattern dirPattern, Path file) {
        return dirPattern.getPath() + "/" + file.getFileName().toString();
    }
}
