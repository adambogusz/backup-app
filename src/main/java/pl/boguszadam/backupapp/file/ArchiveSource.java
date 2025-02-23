package pl.boguszadam.backupapp.file;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

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
                for(Path archive : getMapOfFiles().keySet().stream().sorted().toList()) {
                    if(isCanceled) break;
                    Files.move(archive, Path.of(destinationFolder.toString(), archive.getFileName().toString()));
                    updateProgress(progressStep.getAndIncrement(), getNumberOfArchives());
                }
                updateProgress(1, 1);
                ArchivePackage.isCanceled = false;
                return null;
            }
        };

        progressBar.progressProperty().bind(task.progressProperty());

        new Thread(task).start();
    }
}
