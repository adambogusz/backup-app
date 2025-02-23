package pl.boguszadam.backupapp.file;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public class ArchiveSource extends ArchivePackage {
    public ArchiveSource(Path pathOfZipFile) {
        super(pathOfZipFile);
    }

    public void move(Path destinationFolder, ProgressBar progressBar) {
        AtomicInteger progressStep = new AtomicInteger(1);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                getMapOfFiles()
                        .keySet()
                        .forEach(archive -> {
                            try {
                                Files.move(archive, Path.of(destinationFolder.toString(), archive.getFileName().toString()));
                                updateProgress(progressStep.getAndIncrement(), getNumberOfArchives());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());

        new Thread(task).start();
    }
}
