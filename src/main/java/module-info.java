module pl.boguszadam.backupapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens pl.boguszadam.backupapp to javafx.fxml;
    exports pl.boguszadam.backupapp;
}