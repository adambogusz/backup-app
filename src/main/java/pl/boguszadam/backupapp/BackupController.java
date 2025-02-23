package pl.boguszadam.backupapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import lombok.SneakyThrows;
import pl.boguszadam.backupapp.file.ArchiveDestination;
import pl.boguszadam.backupapp.file.ArchiveSource;
import pl.boguszadam.backupapp.file.Extension;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class BackupController implements Initializable {
    @FXML
    private Label statusText;

    @FXML
    private ComboBox<String> sourceDrives;

    @FXML
    private ComboBox<String> destinationDrives;

    @FXML
    private ListView<String> sourceBackupList;

    @FXML
    private ListView<String> destinationBackupList;

    private final List<ArchiveSource> sourceArchivePackageBackupList = new ArrayList<>();
    private final List<ArchiveDestination> destinationArchivePackageBackupList = new ArrayList<>();

    @FXML
    protected void onBackupButtonClick() {
        statusText.setText("Już kopiujemy:)");

        sourceArchivePackageBackupList.stream()
                .filter(archiveSource -> archiveSource.toString().equals(sourceBackupList.getSelectionModel().getSelectedItem()))
                .forEach(archivePackageSource -> {
                    try {
                        if(archivePackageSource.getSize() * 2L > getDriveEmptySpaceMB((Path.of(destinationDrives.getSelectionModel().getSelectedItem())).toFile())) {
                            ArchiveDestination oldestArchive = destinationArchivePackageBackupList.stream()
                                    .min(Comparator.comparing(ArchiveDestination::getDateOfBackup))
                                    .orElse(new ArchiveDestination(Path.of(destinationDrives.getSelectionModel().getSelectedItem(), "Backup2DVD 2026-02-22 04;00;26 (Pełna).zip")));
                            oldestArchive.delete();
                        }
                        archivePackageSource.move(Path.of(destinationDrives.getSelectionModel().getSelectedItem()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @SneakyThrows
    @FXML
    protected void onSourceDriveChange() {
            fillBackupList(sourceArchivePackageBackupList, sourceBackupList, sourceDrives.getSelectionModel().getSelectedItem(), ArchiveSource::new);
    }

    @SneakyThrows
    @FXML
    protected void onDestinationDriveChange() throws IOException {
        fillBackupList(destinationArchivePackageBackupList, destinationBackupList, destinationDrives.getSelectionModel().getSelectedItem(), ArchiveDestination::new);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillDriveLetters(sourceDrives, "burn");
        fillDriveLetters(destinationDrives, "burn\\burned");
    }

    private <T> void fillBackupList(List<T> archivePackageBackupList, ListView<String> backupList, String pathWithBackups, Function<Path, T> creator) throws IOException {
        try (Stream<Path> files = Files.list(Path.of(pathWithBackups))) {
            files
                    .filter(getExtentionPredicate())
                    .forEach(archivePath -> {
                        archivePackageBackupList.add(creator.apply(archivePath));
                    });
        }
        archivePackageBackupList
                .forEach(archivePackage -> backupList.getItems().add(archivePackage.toString()));

    }

    private Predicate<Path> getExtentionPredicate() {
        return f -> f.toString().toLowerCase().endsWith(Extension.ZIP.getExtension());
    }

    private void fillDriveLetters(ComboBox<String> drives, String sourcePath) {
        Arrays.stream(File.listRoots()).toList().stream()
                .filter(drive -> Files.exists(Path.of(drive + sourcePath)))
                .forEach(drive -> drives.getItems().add(drive.toString() + sourcePath));
    }

    private long getDriveEmptySpaceGB(File drive) throws IOException {
        return getDriveEmptySpaceMB(drive) / 1024;
    }

    private long getDriveEmptySpaceMB(File drive) throws IOException {
        return Files.getFileStore(drive.toPath()).getUsableSpace() / 1024 / 1024;
    }
}