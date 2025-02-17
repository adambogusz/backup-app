package pl.boguszadam.backupapp;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Getter
public class Archives {
    private final Path pathOfZipFile;
    private int size;
    private final Map<Path, Integer> mapOfFiles = new HashMap<>();

    public Archives(Path pathOfZipFile, int size) throws IOException {
        this.pathOfZipFile = pathOfZipFile;
        this.size = size;
        setMapOfFiles();
        setSizeOfMapArchive();
    }

    public Archives(Path pathOfZipFile) throws IOException {
        this(pathOfZipFile, 0);
    }

    private void setMapOfFiles() throws IOException {
        try(Stream<Path> files = Files.list(Path.of(pathOfZipFile.getParent().toString()))) {
            files
                    .filter(file -> file.toString().contains(pathOfZipFile.toString().substring(0, pathOfZipFile.toString().lastIndexOf('.'))))
                    .forEach(file -> {
                        try {
                            mapOfFiles.put(file, (int) Files.size(file) / 1024 / 1024);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    public void setSizeOfMapArchive() {
        size = mapOfFiles.values().stream()
                .reduce(0, Integer::sum);
    }

    @Override
    public String toString() {
        return pathOfZipFile +
                " (" + size + "MB)";
    }
}
