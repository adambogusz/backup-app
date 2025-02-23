package pl.boguszadam.backupapp.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ArchiveSource extends ArchivePackage {
    public ArchiveSource(Path pathOfZipFile) {
        super(pathOfZipFile);
    }

    public void move(Path destinationFolder) {
        getMapOfFiles()
                .keySet()
                .forEach(archive -> {
                    try {
                        Files.move(archive, Path.of(destinationFolder.toString(), archive.getFileName().toString()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
