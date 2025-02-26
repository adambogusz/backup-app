package pl.boguszadam.backupapp.file;

import lombok.Getter;

@Getter
public enum Extension {
    ZIP("zip");

    private final String extension;

    Extension(String extension) {
        this.extension = extension;
    }
}
