package de.twometer.protoedit.ui;

import de.twometer.protoedit.parsers.ParserManager;
import de.twometer.protoedit.util.IOUtil;
import de.twometer.protoedit.util.ResourceLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;

import java.io.File;

public class MainController {

    @FXML
    public CodeArea markdownArea;

    @FXML
    public WebView previewArea;

    private WebEngine engine;

    private Stage stage;

    private String lastSavedContents = "";

    private String currentPath = null;

    @FXML
    public void initialize() {
        engine = previewArea.getEngine();
        engine.setUserStyleSheetLocation(ResourceLoader.getResource("layout/main.css").toString());
        markdownArea.setOnKeyTyped(event -> refreshPreview());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void onNewFile(ActionEvent actionEvent) {
        loadFile("");
    }

    public void onOpenFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown files", "*.md", "*.markdown", "*.mdown"));
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;
        String contents = IOUtil.readFile(file);
        loadFile(contents);
    }

    public void onSaveFile(ActionEvent actionEvent) {
        saveFile();
        lastSavedContents = markdownArea.getText();
    }

    private void loadFile(String contents) {
        if (isDirty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Save current file?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.YES) {
                    saveFile();
                    setContents(contents);
                } else if (rs == ButtonType.NO) {
                    setContents(contents);
                }
            });
        } else setContents(contents);
    }

    private void setContents(String content) {
        markdownArea.replaceText(content);
        lastSavedContents = content;
        refreshPreview();
    }

    private void refreshPreview() {
        String html = ParserManager.getInstance().parse(markdownArea.getText());
        engine.loadContent(html);
    }

    private void saveFile() {
        if (!isDirty()) return;

        if (hasPath()) {
            IOUtil.writeFile(new File(currentPath), markdownArea.getText());
            lastSavedContents = markdownArea.getText();
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save file");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown files", "*.md", "*.markdown", "*.mdown"));
            File file = fileChooser.showSaveDialog(stage);
            if (file == null) return;
            currentPath = file.getAbsolutePath();
            saveFile();
        }
    }

    private boolean isDirty() {
        return !markdownArea.getText().equals(lastSavedContents);
    }

    private boolean hasPath() {
        return currentPath != null && currentPath.trim().length() != 0;
    }

}
