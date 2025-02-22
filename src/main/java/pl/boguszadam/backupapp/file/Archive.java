package pl.boguszadam.backupapp.file;

import java.nio.file.Path;

public interface Archive {
    Path path = null;
    String name = "";
    long size();
}
