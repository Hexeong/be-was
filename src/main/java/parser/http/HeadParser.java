package parser.http;

import java.io.BufferedReader;
import java.io.IOException;

// 고민: 어떻게 같은 interface로 다른 전달인자와 returnType을 제공하여 다형성을 할까?
// 해결: Generic과 다형성을 사용하여 각 Type에 맞는 return Type을 제공하자.
public interface HeadParser<T> {
    T parse(BufferedReader bufRed) throws IOException;
}
