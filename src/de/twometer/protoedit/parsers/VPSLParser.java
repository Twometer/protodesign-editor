package de.twometer.protoedit.parsers;

import de.twometer.protoedit.util.CodeScanner;

import static de.twometer.protoedit.util.ParserUtils.*;

public class VPSLParser implements IParser {

    @Override
    public String process(String html) {
        State state = new State();
        return CodeScanner.scan(html, "vpsl", (data, i, output) -> {
            if (!state.inDataType && startsAt(data, "&lt;", i)) {
                state.inDataType = true;
                state.datatypeApply = i + "&lt;".length();
            }
            if (!state.inDataType && (data[i] == '[' || data[i] == '{') && (i == 0 || (data[i - 1] == '>' || data[i - 1] == ';' || data[i - 1] == '\n') || data[i - 1] == ' ' || data[i - 1] == '\t')) {
                state.inDataType = true;
                state.datatypeApply = i + 1;
            }
            if (startsAt(data, "//", i) && !state.inSingleComment) {
                state.inSingleComment = true;
                output.append(buildHtmlTag("vpsl-comment", true));
            }
            if (startsAt(data, "@", i) && !state.inComment()) {
                state.inAnnotation = true;
                output.append(buildHtmlTag("vpsl-annotation", true));
            }
            if (startsAt(data, "/*", i) && !state.inBlockComment) {
                state.inBlockComment = true;
                output.append(buildHtmlTag("vpsl-comment", true));
            }
            if (state.inAnnotation && (data[i] == '\n' || data[i] == '\r')) {
                state.inAnnotation = false;
                output.append(buildHtmlTag("vpsl-annotation", false));
            }
            if (state.inSingleComment && (data[i] == '\n' || data[i] == '\r')) {
                state.inSingleComment = false;
                output.append(buildHtmlTag("vpsl-comment", false));
            }
            if (state.inDataType && state.datatypeApply == i) {
                output.append(buildHtmlTag("vpsl-datatype", true));
            }
            if (state.inDataType && data[i] == ' ') {
                state.inDataType = false;
                output.append(buildHtmlTag(null, false));
            }

            if (endsAt(data, "*/", i) && state.inBlockComment) {
                state.inBlockComment = false;
                output.append(buildHtmlTag("vpsl-comment", false));
            }

            if (!state.inCondition && i > 0 && data[i] == '(' && data[i - 1] == '(') {
                output.append(buildHtmlTag("vpsl-condition", true));
                state.inCondition = true;
            }

            output.append(data[i]);

            if (state.inCondition && data[i] == ')') {
                output.append(buildHtmlTag(null, false));
                state.inCondition = false;
            }
        });
    }

    private class State {
        boolean inDataType = false;
        boolean inSingleComment = false;
        boolean inBlockComment = false;
        boolean inCondition = false;
        boolean inAnnotation = false;
        int datatypeApply = -1;

        boolean inComment() {
            return inSingleComment || inBlockComment;
        }
    }
}
