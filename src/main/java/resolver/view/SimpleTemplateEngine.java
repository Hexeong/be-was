package resolver.view;

import model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleTemplateEngine {

    private static final Logger log = LoggerFactory.getLogger(SimpleTemplateEngine.class);

    private static final String VARIABLE_OPEN_IDENTIFIER = "{{";
    private static final String VARIABLE_CLOSE_IDENTIFIER = "}}";

    // <if> 태그 상수
    private static final String IF_START_PREFIX = "<if condition=\"";
    private static final String IF_START_SUFFIX = "\">";
    private static final String IF_END_TAG = "</if>";

    // [추가] <for> 태그 상수
    // 예: <for cur="article" range="articles">
    private static final String FOR_START_PREFIX = "<for ";
    private static final String FOR_END_TAG = "</for>";

    /**
     * 1. 변수 치환
     */
    public static String renderVariables(StringBuilder template, Model model) throws IOException {
        int startIndex = template.indexOf(VARIABLE_OPEN_IDENTIFIER);

        while (startIndex != -1) {
            int endIndex = template.indexOf(VARIABLE_CLOSE_IDENTIFIER, startIndex);
            if (endIndex == -1) throw new IOException("잘못된 변수 렌더링 문법: 닫는 괄호가 없습니다.");

            String key = template.substring(startIndex + 2, endIndex).trim();
            Object value = resolveValue(key, model);
            String replacement = (value != null) ? value.toString() : "";

            template.replace(startIndex, endIndex + 2, replacement);
            startIndex = template.indexOf(VARIABLE_OPEN_IDENTIFIER, startIndex + replacement.length());
        }
        return template.toString();
    }

    /**
     * 2. 조건문 처리
     */
    public static void renderConditionals(StringBuilder template, Model model) throws IOException {
        int searchIdx = 0;

        while (true) {
            int startTagIdx = template.indexOf(IF_START_PREFIX, searchIdx);
            if (startTagIdx == -1) break;

            int conditionKeyEndIdx = template.indexOf("\"", startTagIdx + IF_START_PREFIX.length());
            if (conditionKeyEndIdx == -1) throw new IOException("잘못된 조건 렌더링 문법");

            String conditionKey = template.substring(startTagIdx + IF_START_PREFIX.length(), conditionKeyEndIdx);
            int startTagCloseIdx = template.indexOf(IF_START_SUFFIX, conditionKeyEndIdx);
            int endTagIdx = template.indexOf(IF_END_TAG, startTagCloseIdx);

            if (startTagCloseIdx == -1 || endTagIdx == -1) throw new IOException("잘못된 조건 렌더링 문법");

            // 값 조회 (Boolean 여부 확인)
            Object conditionObj = resolveValue(conditionKey, model);
            boolean condition = Boolean.TRUE.equals(conditionObj);

            if (condition) {
                // 태그 제거, 내용은 유지
                template.delete(endTagIdx, endTagIdx + IF_END_TAG.length());
                template.delete(startTagIdx, startTagCloseIdx + IF_START_SUFFIX.length());
                searchIdx = startTagIdx;
            } else {
                // 태그와 내용 모두 제거
                template.delete(startTagIdx, endTagIdx + IF_END_TAG.length());
                searchIdx = startTagIdx;
            }
        }
    }

    /**
     * 3. 반복문 처리
     */
    public static void renderLoops(StringBuilder template, Model model) throws IOException {
        int searchIdx = 0;

        while (true) {
            // 1. <for 태그 시작 위치 찾기
            int startTagIdx = template.indexOf(FOR_START_PREFIX, searchIdx);
            if (startTagIdx == -1) break;

            // 2. 태그 닫는 괄호 '>' 위치 찾기
            int startTagCloseIdx = template.indexOf(">", startTagIdx);
            if (startTagCloseIdx == -1) throw new IOException("잘못된 반복문 문법: 태그가 닫히지 않았습니다.");

            // 3. 속성 파싱 (cur="...", range="...")
            String tagAttributes = template.substring(startTagIdx, startTagCloseIdx);
            String curVarName = extractAttribute(tagAttributes, "cur");
            String rangeKey = extractAttribute(tagAttributes, "range");

            if (curVarName == null || rangeKey == null) {
                throw new IOException("잘못된 반복문 문법: cur 또는 range 속성이 누락되었습니다.");
            }

            // 4. </for> 닫는 태그 위치 찾기
            int endTagIdx = template.indexOf(FOR_END_TAG, startTagCloseIdx);
            if (endTagIdx == -1) throw new IOException("잘못된 반복문 문법: </for> 가 없습니다.");

            // 5. 반복할 본문 내용 추출
            String bodyTemplate = template.substring(startTagCloseIdx + 1, endTagIdx);

            // 6. 리스트 데이터 가져오기
            Object rangeObj = resolveValue(rangeKey, model);
            StringBuilder sb = new StringBuilder();

            if (rangeObj instanceof Collection<?> collection) {
                // 7. 컬렉션 순회
                for (Object item : collection) {
                    // 현재 아이템을 'cur' 변수명으로 모델에 잠시 저장
                    model.put(curVarName, item);

                    // 본문 내용을 복사해서 렌더링 (재귀적 처리 효과)
                    StringBuilder currentBody = new StringBuilder(bodyTemplate);

                    // [중요] 루프 내부의 변수({{cur.name}})와 조건문(<if>)을 여기서 즉시 처리
                    renderVariables(currentBody, model);
                    renderConditionals(currentBody, model);

                    sb.append(currentBody);
                }
                // 루프 종료 후 임시 변수 제거 (선택사항, 안전을 위해 권장)
                // model.remove(curVarName); // Model에 remove가 있다면 사용
            }

            // 8. 원본 템플릿의 <for>...</for> 전체를 생성된 문자열로 교체
            template.replace(startTagIdx, endTagIdx + FOR_END_TAG.length(), sb.toString());

            // 다음 검색 위치 갱신 (교체된 길이만큼 이동하지 않고, 교체된 위치부터 다시 검색하여 중첩 처리 가능성 열어둠)
            // 다만 단순 구현에서는 startTagIdx로 두어도 무방합니다.
            searchIdx = startTagIdx + sb.length();
        }
    }

    // [헬퍼] 속성 값 추출 (예: cur="item" 에서 item 추출)
    private static String extractAttribute(String text, String key) {
        String pattern = key + "=\"([^\"]*)\""; // key="value" 패턴
        Matcher matcher = Pattern.compile(pattern).matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    // [헬퍼] 객체 값 조회 (Dot Notation 지원)
    private static Object resolveValue(String key, Model model) {
        if (key == null || key.isBlank()) return null;

        if (!key.contains(".")) {
            return model.get(key);
        }

        String[] parts = key.split("\\.");
        Object currentObject = model.get(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            if (currentObject == null) return null;
            currentObject = getFieldValue(currentObject, parts[i]);
        }

        return currentObject;
    }

    // [헬퍼] 리플렉션 Getter 호출
    private static Object getFieldValue(Object object, String fieldName) {
        try {
            Class<?> clazz = object.getClass();
            String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            Method method = clazz.getMethod(getterName);
            return method.invoke(object);
        } catch (Exception e) {
            log.warn("Field resolution failed: {}.{} - {}", object.getClass().getSimpleName(), fieldName, e.getMessage());
            return null;
        }
    }
}