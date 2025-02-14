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
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class BackupController implements Initializable {
    @FXML
    private Label welcomeText;

    @FXML
    private ComboBox<String> sourceDrives;

    @FXML
    private ComboBox<String> destinationDrives;

    @FXML
    private ListView<String> sourceBackupList;

    @FXML
    private ListView<String> destinationBackupList;

    @FXML
    protected void onHelloButtonClick()  {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onSourceDriveChange() throws IOException {
        fillBackupList(sourceBackupList, sourceDrives.getSelectionModel().getSelectedItem());
    }

    @FXML
    protected void onDestinationDriveChange() throws IOException {
        fillBackupList(destinationBackupList, destinationDrives.getSelectionModel().getSelectedItem());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillDriveLetters(sourceDrives, "burn");
        fillDriveLetters(destinationDrives, "ADE");
    }

    private void fillBackupList(ListView<String> backupList, String pathToBackups) throws IOException {
        try (Stream<Path> files = Files.list(Path.of(pathToBackups))) {
            files.filter(getExtentionPredicate())
                    .forEach(f -> {
                        backupList.getItems().add(f.toString());
                    });
        }
    }

    private Predicate<Path> getExtentionPredicate() {
        return f -> f.toString().toLowerCase().endsWith(".001");
    }

    private void fillDriveLetters(ComboBox<String> drives, String sourcePath) {
        Arrays.stream(File.listRoots()).toList().stream()
                .filter(drive -> Files.exists(Path.of(drive + sourcePath)))
                .forEach(drive -> drives.getItems().add(drive.toString() + sourcePath));
    }
}