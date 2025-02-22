package pl.boguszadam.backupapp.file;

import java.io.IOException;
import java.nio.file.Path;

public class ArchiveDestination extends ArchivePackage {
    public ArchiveDestination(Path pathOfZipFile) throws IOException {
        super(pathOfZipFile);
    }

    public void delete() {

    }
}
