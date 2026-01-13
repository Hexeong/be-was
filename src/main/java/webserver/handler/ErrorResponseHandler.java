package webserver.handler;

import exception.CustomException;
import model.Model;
import model.http.HttpResponse;
import model.http.HttpStatus;
import resolver.view.ModelAndView;

import java.io.IOException;

public class ErrorResponseHandler {
    /**
     * 들어온 res 객체에 대하여 예외 정보를 담은 객체 e의 정보를 /error.html에 렌더링하여 응답한다.
     * @param res
     * @param e
     * @throws IOException
     */
    public static void responseError(HttpResponse res, CustomException e) throws IOException {
        if (res.getStatus().equals(HttpStatus.OK)) {
            Model model = new Model(e);
            ModelAndView mv = new ModelAndView(model, "/error.html");
            mv.resolve(res);
        }

        res.sendResponse();
    }
}
