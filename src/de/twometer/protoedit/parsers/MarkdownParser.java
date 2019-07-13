package de.twometer.protoedit.parsers;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownParser implements IParser {
    @Override
    public String process(String html) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(html);
        HtmlRenderer renderer = HtmlRenderer.builder().escapeHtml(false).build();
        return renderer.render(document);
    }
}
