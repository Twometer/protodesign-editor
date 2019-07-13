package de.twometer.protoedit.ui;

import de.twometer.protoedit.parsers.ParserManager;
import de.twometer.protoedit.util.ResourceLoader;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.fxmisc.richtext.CodeArea;

public class MainController {

    @FXML
    public CodeArea markdownArea;

    @FXML
    public WebView previewArea;

    @FXML
    public void initialize() {
        WebEngine engine = previewArea.getEngine();
        engine.setUserStyleSheetLocation(ResourceLoader.getResource("layout/main.css").toString());
        markdownArea.setOnKeyTyped(event -> {
            String html = ParserManager.getInstance().parse(markdownArea.getText());
            engine.loadContent(html);
        });
    }

}
