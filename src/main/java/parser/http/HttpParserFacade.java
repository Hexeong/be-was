package parser.http;

import model.http.HttpBody;
import model.http.HttpHeader;
import model.http.HttpStartLine;
import model.http.TotalHttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.http.impl.HttpParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpParserFacade {
    private static final Logger log = LoggerFactory.getLogger(HttpParserFacade.class);

    private final HeadParser<HttpStartLine> startLineParser;
    private final HeadParser<HttpHeader> headerParser;
    private final ContentParser<HttpBody> bodyParser;

    private static volatile HttpParserFacade instance = null;

    // 고민: 기존 facade가 싱글톤이기에 각 Parser 또한 기본 생성자로 객체를 생성해도 값이 사라지지 않음,
    //      근데 다른 곳에서 호출할 경우 Parser의 경우 싱글톤이 보장이 안됨.
    // 해결: 다른 곳에서 호출 못하게 protected로 하는건?? 그리고 Factory 클래스를 생성하여 factory로만 접근한다면
    //      위의 문제도 해결이 가능하다.
    // 고민: 그럼 각 parser에 대해서도 재사용을 위해 싱글턴을 적용해야 하는가?
    // 해결: 아니다. Facade 객체가 살아있는 동안, 멤버 변수도 모두 살아있기에 protected 생성자만으로도 충분히 기존의 전략 달성이 가능하다.
    private HttpParserFacade() {
        HttpParserFactory factory = new HttpParserFactory();
        this.startLineParser = factory.getStartLineParser();
        this.headerParser = factory.getHeaderParser();
        this.bodyParser = factory.getBodyParser();
    }

    public static HttpParserFacade getInstance() {
        if (instance == null) {
            synchronized (HttpParserFacade.class) {
                if (instance == null) {
                    instance = new HttpParserFacade();
                }
            }
        }
        return instance;
    }

    public TotalHttpMessage parse(InputStream in) {
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader bufRed = new BufferedReader(isr);

        try {
            HttpStartLine startLine = startLineParser.parse(bufRed);
            HttpHeader header = headerParser.parse(bufRed);
            HttpBody body = bodyParser.parse(bufRed, header);

            return new TotalHttpMessage(
                    startLine,
                    header,
                    body
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            return new TotalHttpMessage();
        }
    }
}
