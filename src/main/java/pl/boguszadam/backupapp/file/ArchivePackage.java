package pl.boguszadam.backupapp.file;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Getter
public abstract class ArchivePackage implements Archive {
    private final Path pathOfZipFile;
    private long size;
    private int numberOfArchives;
    private Map<Path, Long> mapOfFiles = new HashMap<>();
    public static boolean isCanceled = false;

    public ArchivePackage(Path pathOfZipFile) {
        this.pathOfZipFile = pathOfZipFile;
        addArchivesToMap();
        this.size = countSize();
        this.numberOfArchives = countNumberOfArchives();
    }

    private void addArchivesToMap() {
        try(Stream<Path> files = Files.list(Path.of(pathOfZipFile.getParent().toString()))) {
            files
                    .filter(file -> file.toString().contains(pathOfZipFile.toString().substring(0, pathOfZipFile.toString().lastIndexOf('.'))))
                    .forEach(file -> {
                        try {
                            mapOfFiles.put(file, Files.size(file) / 1024 / 1024);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return pathOfZipFile +
                " (" + size + "MB)";
    }

    @Override
    public long countSize() {
        return mapOfFiles.values().stream()
                .reduce(0L, Long::sum);
    }

    @Override
    public int countNumberOfArchives() {
        return mapOfFiles.size();
    }
}
