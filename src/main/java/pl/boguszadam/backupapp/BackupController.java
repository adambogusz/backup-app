package pl.boguszadam.backupapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import pl.boguszadam.backupapp.file.ArchiveDestination;
import pl.boguszadam.backupapp.file.ArchivePackage;
import pl.boguszadam.backupapp.file.ArchiveSource;

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
    private final String extensionOfMainArchive = ".001";

    @FXML
    protected void onBackupButtonClick() throws IOException {
        statusText.setText("Już kopiujemy:)");

        sourceArchivePackageBackupList
                .forEach(archivePackageSource -> {
                    try {
                        if(archivePackageSource.getSize() * 2L < getDriveEmptySpaceMB((Path.of(destinationDrives.getSelectionModel().getSelectedItem())).toFile())) {
                            destinationArchivePackageBackupList
                                    .forEach(archiveDestination -> System.out.println(archiveDestination.getSize()));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
//        sourceArchivePackageBackupList.stream()
//                .map(ArchivePackage::getMapOfFiles)
//                .forEach(file ->);
    }

    @FXML
    protected void onSourceDriveChange() throws IOException {
        fillSourceBackupList(sourceBackupList, sourceDrives.getSelectionModel().getSelectedItem());
    }

    @FXML
    protected void onDestinationDriveChange() throws IOException {
        fillDestinationBackupList(destinationBackupList, destinationDrives.getSelectionModel().getSelectedItem());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillDriveLetters(sourceDrives, "burn");
        fillDriveLetters(destinationDrives, "burn\\burned");
    }

    private void fillSourceBackupList(ListView<String> backupList, String pathWithBackups) throws IOException {
        try (Stream<Path> files = Files.list(Path.of(pathWithBackups))) {
            files
                    .filter(getExtentionPredicate())
                    .forEach(archivePath -> {
                        try {
                            sourceArchivePackageBackupList.add(new ArchiveSource(archivePath));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        sourceArchivePackageBackupList
                .forEach(archivePackage -> backupList.getItems().add(archivePackage.toString()));

    }

    private void fillDestinationBackupList(ListView<String> backupList, String pathWithBackups) throws IOException {
        try (Stream<Path> files = Files.list(Path.of(pathWithBackups))) {
            files
                    .filter(getExtentionPredicate())
                    .forEach(archivePath -> {
                        try {
                            destinationArchivePackageBackupList.add(new ArchiveDestination(archivePath));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        destinationArchivePackageBackupList
                .forEach(archivePackage -> backupList.getItems().add(archivePackage.toString()));
    }

    private Predicate<Path> getExtentionPredicate() {
        return f -> f.toString().toLowerCase().endsWith(extensionOfMainArchive);
    }

    private void fillDriveLetters(ComboBox<String> drives, String sourcePath) {
        Arrays.stream(File.listRoots()).toList().stream()
                .filter(drive -> Files.exists(Path.of(drive + sourcePath)))
                .forEach(drive -> {
                    drives.getItems().add(drive.toString() + sourcePath);
                });
    }

    private long getDriveEmptySpaceGB(File drive) throws IOException {
        return getDriveEmptySpaceMB(drive) / 1024;
    }

    private long getDriveEmptySpaceMB(File drive) throws IOException {
        return Files.getFileStore(drive.toPath()).getUsableSpace() / 1024 / 1024;
    }
}