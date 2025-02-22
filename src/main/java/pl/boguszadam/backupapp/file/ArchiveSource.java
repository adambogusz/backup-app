package pl.boguszadam.backupapp.file;

import java.io.IOException;
import java.nio.file.Path;

public class ArchiveSource extends ArchivePackage {
    public ArchiveSource(Path pathOfZipFile) throws IOException {
        super(pathOfZipFile);
    }

    public void move() {

    }
}
