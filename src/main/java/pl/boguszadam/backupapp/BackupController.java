package pl.boguszadam.backupapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.SneakyThrows;
import pl.boguszadam.backupapp.file.ArchiveDestination;
import pl.boguszadam.backupapp.file.ArchivePackage;
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

@Getter
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

    @FXML
    private ProgressBar progressBar;

    private final List<ArchiveSource> sourceArchivePackageBackupList = new ArrayList<>();
    private final List<ArchiveDestination> destinationArchivePackageBackupList = new ArrayList<>();

    @FXML
    protected void onBackupButtonClick() {
        statusText.setText("Kopiowanie w toku :)");

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
                        archivePackageSource.move(Path.of(destinationDrives.getSelectionModel().getSelectedItem()), progressBar);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @FXML
    protected void onCancelButtonClick() {
        ArchivePackage.isCanceled = true;
        statusText.setText("Przerywanie kopiowania w toku");
    }

    @SneakyThrows
    @FXML
    protected void onSourceDriveChange() {
        refreshBackupList(sourceArchivePackageBackupList, sourceBackupList, sourceDrives.getSelectionModel().getSelectedItem(), ArchiveSource::new);
    }

    @SneakyThrows
    @FXML
    protected void onDestinationDriveChange() {
        refreshBackupList(destinationArchivePackageBackupList, destinationBackupList, destinationDrives.getSelectionModel().getSelectedItem(), ArchiveDestination::new);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillDriveLetters(sourceDrives, "burn");
        fillDriveLetters(destinationDrives, "burn\\burned");
        progressBar.setProgress(0.0d);
    }

    private <T> void refreshBackupList(List<T> archivePackageBackupList, ListView<String> backupList, String pathWithBackups, Function<Path, T> creator) throws IOException {
        clearListView(archivePackageBackupList, backupList);
        fillPackageList(archivePackageBackupList, pathWithBackups, creator);
        archivePackageBackupList
                .forEach(archivePackage -> backupList.getItems().add(archivePackage.toString()));

    }

    private <T> void fillPackageList(List<T> archivePackageBackupList, String pathWithBackups, Function<Path, T> creator) throws IOException {
        try (Stream<Path> files = Files.list(Path.of(pathWithBackups))) {
            files
                    .filter(getExtentionPredicate())
                    .forEach(archivePath -> archivePackageBackupList.add(creator.apply(archivePath)));
        }
    }

    private <T> void clearListView(List<T> archivePackageBackupList, ListView<String> backupList) {
        archivePackageBackupList.clear();
        backupList.getItems().clear();
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