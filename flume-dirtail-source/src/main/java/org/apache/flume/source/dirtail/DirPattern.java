package org.apache.flume.source.dirtail;

import java.nio.file.Path;
import java.util.regex.Pattern;

public class DirPattern {
    private String  path;
    private Pattern filePattern;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Pattern getFilePattern() {
        return filePattern;
    }

    public void setFilePattern(Pattern filePattern) {
        this.filePattern = filePattern;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = Pattern.compile(filePattern);
    }

    public boolean isMatchFile(Path path) {
        return filePattern.matcher(path.getFileName().toString()).find();
    }
}
