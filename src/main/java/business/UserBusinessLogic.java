package business;

import db.Database;
import model.http.TotalHttpMessage;
import model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import writer.ResponseWriterFacade;

import java.io.OutputStream;
import java.util.Map;

public class UserBusinessLogic {
    private static final Logger log = LoggerFactory.getLogger(UserBusinessLogic.class);

    public void createUser(OutputStream out, TotalHttpMessage msg) {
        Map<String, String> formData = msg.body().getParsedBody(Map.class);

        User user = new User(
                formData.getOrDefault("userId", ""),
                formData.getOrDefault("password", ""),
                formData.getOrDefault("name", ""),
                formData.getOrDefault("email", "")
        );
        Database.addUser(user);

        log.debug(user.toString());

        ResponseWriterFacade.sendRedirect(out, "/index.html");
    }

    public void login(OutputStream out, TotalHttpMessage msg) {
        Map<String, String> formData = msg.body().getParsedBody(Map.class);

        User findUser = Database.findUserById(formData.getOrDefault("userId", ""));
        if (findUser != null && findUser.getPassword().equals(formData.getOrDefault("password", ""))) {
            ResponseWriterFacade.sendRedirectWithSessionCookie(out, "/index.html");
            log.debug("로그인 성공");
            return;
        }

        log.debug("로그인 실패! \n" + findUser + "\n" + msg.body().getBodyText());
        ResponseWriterFacade.send404NotFoundResponse(out);
    }

    // TODO:: logout 구현, 동적 HTML 다룰 때 함께 다룰 예정
}
