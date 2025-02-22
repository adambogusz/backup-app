package pl.boguszadam.backupapp.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ArchiveDestination extends ArchivePackage {
    public ArchiveDestination(Path pathOfZipFile) throws IOException {
        super(pathOfZipFile);
    }

    public void delete() {
        getMapOfFiles().keySet().forEach(archive -> {
            try {
                Files.deleteIfExists(archive);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
