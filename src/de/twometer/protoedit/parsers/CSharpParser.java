package de.twometer.protoedit.parsers;

import de.twometer.protoedit.util.CodeScanner;

import java.util.Arrays;
import java.util.List;

import static de.twometer.protoedit.util.ParserUtils.*;

public class CSharpParser implements IParser {

    private static final List<String> keywords = Arrays.asList("abstract", "add", "as", "ascending", "async", "await", "base", "bool", "break", "by", "byte", "case", "catch", "char", "checked", "class", "const", "continue", "decimal", "default", "delegate", "descending", "do", "double", "dynamic", "else", "enum", "event", "equals", "explicit", "extern", "false", "finally", "fixed", "float", "for", "foreach", "from", "get", "global", "goto", "group", "if", "implicit", "in", "int", "interface", "internal", "into", "is", "join", "let", "lock", "long", "namespace", "new", "null", "object", "on", "operator", "orderby", "out", "override", "params", "partial", "private", "protected", "public", "readonly", "ref", "remove", "return", "sbyte", "sealed", "select", "set", "short", "sizeof", "stackalloc", "static", "string", "struct", "switch", "this", "throw", "true", "try", "typeof", "uint", "ulong", "unchecked", "unsafe", "ushort", "using", "value", "var", "virtual", "void", "volatile", "where", "while", "yield", "#region", "#endregion");
    private static final List<String> typeKeywords = Arrays.asList("class", "interface", "enum");

    @Override
    public String process(String html) {
        State state = new State();
        return CodeScanner.scan(html, "csharp", (data, i, output) -> {
            String keyword = anyStartsAt(data, keywords, i);
            String typeKeyword = anyStartsAt(data, typeKeywords, i);
            if (keyword != null && state.closeTag == -1 && (isValidKeywordStarter(data, i)) && isValidKeywordEnd(data, i, keyword) && !state.inComment() && !state.inString) {
                if (typeKeyword != null && !state.waitingEndOfType)
                    state.shouldBeType = true;
                output.append(buildHtmlTag("csharp-keyword", true));
                state.closeTag = i + keyword.length() - 1;
            }
            if (startsAt(data, "//", i) && !state.inSingleComment) {
                state.inSingleComment = true;
                output.append(buildHtmlTag("csharp-comment", true));
            }
            if (startsAt(data, "/*", i) && !state.inBlockComment) {
                state.inBlockComment = true;
                output.append(buildHtmlTag("csharp-comment", true));
            }
            if (state.inSingleComment && (data[i] == '\n' || data[i] == '\r')) {
                state.inSingleComment = false;
                output.append(buildHtmlTag("csharp-comment", false));
            }

            if (!state.inAnnotation && i > 0 && data[i - 1] == '[' && !state.inComment()) {
                state.inAnnotation = true;
                output.append(buildHtmlTag("csharp-datatype", true));
            }

            if (startsAt(data, "&quot;", i) && !state.inString && !state.inComment()) {
                state.inString = true;
                state.inStringTF = i + 8;
                output.append(buildHtmlTag("csharp-string", true));
            }
            output.append(data[i]);
            if (state.inAnnotation && i + 1 < data.length && data[i + 1] == ']' && !state.inComment()) {
                state.inAnnotation = false;
                output.append(buildHtmlTag("csharp-datatype", false));
            }

            if (endsAt(data, "*/", i) && state.inBlockComment) {
                state.inBlockComment = false;
                output.append(buildHtmlTag("csharp-comment", false));
            }
            if (endsAt(data, "&quot;", i) && state.inString && i > state.inStringTF) {
                state.inString = false;
                output.append(buildHtmlTag("csharp-string", false));
            }
            if (state.closeTag == i && state.closeTag > 0) {
                output.append(buildHtmlTag("csharp-keyword", false));
                state.closeTag = -1;
            }
            if (!Character.isLetterOrDigit(data[i]) && state.waitingEndOfType) {
                state.waitingEndOfType = false;
                output.append(buildHtmlTag("csharp-datatype", false));
            }
            if (data[i] == ' ' && state.shouldBeType) {
                output.append(buildHtmlTag("csharp-datatype", true));
                state.shouldBeType = false;
                state.waitingEndOfType = true;
            }
        });
    }

    private boolean isValidKeywordStarter(char[] data, int idx) {
        char i = data[idx - 1];
        return i == '\n' || i == ' ' || i == ';' || i == '>' || i == '(';
    }

    private boolean isValidKeywordEnd(char[] data, int idx, String kw) {
        char i = data[idx + kw.length()];
        return i == '\n' || i == ' ' || i == ';';
    }

    private class State {
        int closeTag = -1;
        boolean inSingleComment = false;
        boolean inBlockComment = false;
        boolean inString = false;
        boolean inAnnotation = false;
        boolean shouldBeType = false;
        boolean waitingEndOfType = false;
        int inStringTF = 0;

        boolean inComment() {
            return inSingleComment || inBlockComment;
        }
    }
}
