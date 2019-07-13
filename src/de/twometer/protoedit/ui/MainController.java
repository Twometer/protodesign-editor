package de.twometer.protoedit.ui;

import de.twometer.protoedit.parsers.ParserManager;
import de.twometer.protoedit.util.IOUtil;
import de.twometer.protoedit.util.ResourceLoader;
import de.twometer.protoedit.util.RetentionFileChooser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;

public class MainController {

    @FXML
    public TextArea markdownArea;

    @FXML
    public WebView previewArea;

    private WebEngine engine;

    private Stage stage;

    private String lastSavedContents = "";

    private String currentPath = null;

    private Text text = new Text();

    private Bounds bounds;

    @FXML
    public void initialize() {
        engine = previewArea.getEngine();
        engine.setUserStyleSheetLocation(ResourceLoader.getResource("layout/main.css").toString());
        markdownArea.setOnKeyTyped(event -> refreshPreview());
        markdownArea.setWrapText(true);
        text.fontProperty().bind(markdownArea.fontProperty());
        text.textProperty().bind(markdownArea.textProperty());
        text.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> bounds = newValue);
        markdownArea.scrollTopProperty().addListener((observable, oldValue, newValue) -> {
            double previewHeight = Double.parseDouble(previewArea.getEngine().executeScript("window.getComputedStyle(document.body, null).getPropertyValue('height')").toString().replace("px", ""));
            double areaHeight = bounds.getHeight() * 2;
            double pcx = newValue.doubleValue() / areaHeight;
            double newY = pcx * previewHeight;
            previewArea.getEngine().executeScript("window.scrollTo(0, " + newY + ");");
        });

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void onNewFile(ActionEvent actionEvent) {
        loadFile("");
    }

    public void onOpenFile(ActionEvent actionEvent) {
        File file = RetentionFileChooser.showOpenDialog(stage);
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
        markdownArea.setText(content);
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
            File file = RetentionFileChooser.showSaveDialog(stage);
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
