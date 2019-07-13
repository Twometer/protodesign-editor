package de.twometer.protoedit.parsers;

public class ParserManager {

    private static ParserManager instance;

    private IParser[] registeredParsers = new IParser[]{
            new MarkdownParser(),
            new VPSLParser(),
            new CSharpParser()
    };

    public static ParserManager getInstance() {
        if (instance == null) instance = new ParserManager();
        return instance;
    }

    public String parse(String html) {
        for (IParser parser : registeredParsers) {
            html = parser.process(html);
        }
        return html;
    }

}
