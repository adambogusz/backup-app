package pl.boguszadam.backupapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
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

    private final List<Archives> sourceArchivesBackupList = new ArrayList<>();
    private final List<Archives> destinationArchivesBackupList = new ArrayList<>();
    private final String extensionOfMainArchive = ".001";

    @FXML
    protected void onBackupButtonClick()  {
        statusText.setText("To jeszcze nie działa, ale z czasem zacznie:)");
    }

    @FXML
    protected void onSourceDriveChange() throws IOException {
        fillBackupList(sourceBackupList, sourceArchivesBackupList, sourceDrives.getSelectionModel().getSelectedItem());
    }

    @FXML
    protected void onDestinationDriveChange() throws IOException {
        fillBackupList(destinationBackupList, destinationArchivesBackupList, destinationDrives.getSelectionModel().getSelectedItem());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillDriveLetters(sourceDrives, "burn");
        fillDriveLetters(destinationDrives, "");
    }

    private void fillBackupList(ListView<String> backupList, List<Archives> archivesBackupList, String pathToBackups) throws IOException {
        try (Stream<Path> files = Files.list(Path.of(pathToBackups.substring(0, pathToBackups.lastIndexOf(" ("))))) {
            files
                    .filter(getExtentionPredicate())
                    .forEach(archive -> {
                        try {
                            archivesBackupList.add(new Archives(archive));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        archivesBackupList.forEach(archive -> backupList.getItems().add(archive.toString()));
    }

    private Predicate<Path> getExtentionPredicate() {
        return f -> f.toString().toLowerCase().endsWith(extensionOfMainArchive);
    }

    private void fillDriveLetters(ComboBox<String> drives, String sourcePath) {
        Arrays.stream(File.listRoots()).toList().stream()
                .filter(drive -> Files.exists(Path.of(drive + sourcePath)))
                .forEach(drive -> {
                    try {
                        drives.getItems().add(drive.toString() + sourcePath + " (wolne: " + (Files.getFileStore(drive.toPath()).getUsableSpace() / (1024 * 1024 * 1024)) + " GB)");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}