package de.twometer.protoedit.util;

import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class RetentionFileChooser {
    private static FileChooser instance = null;
    private static SimpleObjectProperty<File> lastKnownDirectoryProperty = new SimpleObjectProperty<>();

    // From https://stackoverflow.com/questions/36920131/can-a-javafx-filechooser-remember-the-last-directory-it-opened

    private RetentionFileChooser() {
    }

    private static FileChooser getInstance() {
        if (instance == null) {
            instance = new FileChooser();
            instance.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
            //Set the FileExtensions you want to allow
            instance.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown files", "*.md", "*.markdown", "*.mdown"));
        }
        return instance;
    }

    public static File showOpenDialog() {
        return showOpenDialog(null);
    }

    public static File showOpenDialog(Window ownerWindow) {
        File chosenFile = getInstance().showOpenDialog(ownerWindow);
        if (chosenFile != null) {
            //Set the property to the directory of the chosenFile so the fileChooser will open here next
            lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
        }
        return chosenFile;
    }

    public static File showSaveDialog() {
        return showSaveDialog(null);
    }

    public static File showSaveDialog(Window ownerWindow) {
        File chosenFile = getInstance().showSaveDialog(ownerWindow);
        if (chosenFile != null) {
            //Set the property to the directory of the chosenFile so the fileChooser will open here next
            lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
        }
        return chosenFile;
    }
}
