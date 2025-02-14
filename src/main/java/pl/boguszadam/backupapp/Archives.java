package pl.boguszadam.backupapp;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Archives {
    private String path;
    private String size;
    private Map<Path, Integer> mapOfFiles = new HashMap<>();

    public Archives(String path, String size, Map<Path, Integer> mapOfFiles) {
        this.path = path;
        this.size = size;
        this.mapOfFiles = mapOfFiles;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Map<Path, Integer> getMapOfFiles() {
        return mapOfFiles;
    }

    public void setMapOfFiles(Map<Path, Integer> mapOfFiles) {
        this.mapOfFiles = mapOfFiles;
    }

    @Override
    public String toString() {
        return "Backup{" +
                "path='" + path + '\'' +
                ", size='" + size + '\'' +
                ", listOfFiles=" + mapOfFiles +
                '}';
    }
}
