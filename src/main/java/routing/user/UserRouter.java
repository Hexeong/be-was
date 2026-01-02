package routing.user;

import db.Database;
import model.http.HttpStatus;
import model.user.User;
import model.http.TotalHttpMessage;
import model.http.sub.RequestMethod;
import routing.DomainRouter;
import writer.ResponseBodyWriter;
import writer.ResponseHeaderWriter;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.BiConsumer;

public class UserRouter implements DomainRouter {
    private static final String REDIRECT_HEADER_KEY = "Location";
    private static final String REDIRECT_HEADER_VALUE = "/index.html";

    public UserRouter() {}

    public boolean route(OutputStream out, TotalHttpMessage message) {
        for (UserPath mapping : UserPath.values()) {
            if (mapping.path.equals(message.line().getPathUrl())
                    && mapping.method.equals(message.line().getMethod())) {

                mapping.handler.accept(out, message);
                return true;
            }
        }
        return false;
    }

    private enum UserPath {
        CREATE("/user/create", RequestMethod.GET, (out, msg) -> {
            Map<String, Object> queryParmeterList = msg.line().getQueryParameterList();

            // TODO:: 비즈니스 로직 분리하기
            User user = new User(
                    queryParmeterList.getOrDefault("userId", "").toString(),
                    queryParmeterList.getOrDefault("password", "").toString(),
                    queryParmeterList.getOrDefault("name", "").toString(),
                    queryParmeterList.getOrDefault("email", "").toString()
            );

            Database.addUser(user);

            // TODO:: Content-Type도 Enum 처리하기
            DataOutputStream dos = new DataOutputStream(out);
            ResponseHeaderWriter.writeHeader(
                    dos,
                    Map.of(REDIRECT_HEADER_KEY, REDIRECT_HEADER_VALUE),
                    0,
                    HttpStatus.FOUND);

            ResponseBodyWriter.writeBody(dos, new byte[0]);
        });

        private final String path;
        private final RequestMethod method;
        private final BiConsumer<OutputStream, TotalHttpMessage> handler;

        UserPath(String path, RequestMethod method, BiConsumer<OutputStream, TotalHttpMessage> handler) {
            this.path = path;
            this.method = method;
            this.handler = handler;
        }
    }
}
