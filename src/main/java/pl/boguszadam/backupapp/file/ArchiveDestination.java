package pl.boguszadam.backupapp.file;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class ArchiveDestination extends ArchivePackage {
    private final Integer dateOfBackup;

    public ArchiveDestination(Path pathOfZipFile) throws IOException {
        super(pathOfZipFile);
        this.dateOfBackup = Integer.parseInt(pathOfZipFile
                .getFileName()
                .toString()
                .substring(11, 18)
                .replace("-", ""));
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
