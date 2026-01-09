package resolver.view;

import model.Model;

import java.io.IOException;

public class SimpleTemplateEngine { // By [AI]

    private static final String VARIABLE_OPEN_IDENTIFIER = "{{";
    private static final String VARIABLE_CLOSE_IDENTIFIER = "}}";

    private static final String START_TAG_PREFIX = "<if condition=\"";
    private static final String START_TAG_SUFFIX = "\">";
    private static final String END_TAG = "</if>";

    // 1. 변수 치환 로직 (예: {{username}} -> "Inseong")
    public static String renderVariables(StringBuilder template, Model model) throws IOException {
        int startIndex = template.indexOf(VARIABLE_OPEN_IDENTIFIER);

        while (startIndex != -1) {
            int endIndex = template.indexOf(VARIABLE_CLOSE_IDENTIFIER, startIndex);
            if (endIndex == -1) throw new IOException("잘못된 변수 렌더링 문법");

            // 키 추출 ("{{username}}" -> "username")
            String key = template.substring(startIndex + 2, endIndex).trim();
            Object value = model.get(key);
            String replacement = (value != null) ? value.toString() : "";

            template.replace(startIndex, endIndex + 2, replacement);

            // 다음 검색 위치 조정
            startIndex = template.indexOf("{{", startIndex + replacement.length());
        }
        return template.toString();
    }

    // 2. 조건문 처리 로직 (예: <if condition="isLoggedIn">...</if>)
    public static void renderConditionals(StringBuilder template, Model model) throws IOException {

        int searchIdx = 0;

        while (true) {
            int startTagIdx = template.indexOf(START_TAG_PREFIX, searchIdx);

            if (startTagIdx == -1) break;

            int conditionKeyEndIdx = template.indexOf("\"", startTagIdx + START_TAG_PREFIX.length());
            if (conditionKeyEndIdx == -1)
                throw new IOException("잘못된 조건 렌더링 문법");

            String conditionKey = template.substring(startTagIdx + START_TAG_PREFIX.length(), conditionKeyEndIdx);

            int startTagCloseIdx = template.indexOf(START_TAG_SUFFIX, conditionKeyEndIdx);
            if (startTagCloseIdx == -1)
                throw new IOException("잘못된 조건 렌더링 문법");

            int endTagIdx = template.indexOf(END_TAG, startTagCloseIdx);
            if (endTagIdx == -1)
                throw new IOException("잘못된 조건 렌더링 문법");

            boolean condition = Boolean.TRUE.equals(model.get(conditionKey));

            if (condition) {
                // [True] 태그 껍데기만 삭제하고 내용은 유지, 뒤에서부터 지워야 인덱스가 꼬이지 않음
                template.delete(endTagIdx, endTagIdx + END_TAG.length()); // </if> 삭제
                template.delete(startTagIdx, startTagCloseIdx + START_TAG_SUFFIX.length()); // <if ...> 삭제

                searchIdx = startTagIdx;
            } else {
                // [False] 태그 포함 통째로 삭제
                template.delete(startTagIdx, endTagIdx + END_TAG.length());
                searchIdx = startTagIdx;
            }
        }
    }
}