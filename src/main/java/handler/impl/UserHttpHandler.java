package handler.impl;

import annotation.*;
import dao.UserDao;
import db.SessionStorage;
import exception.CustomException;
import exception.ErrorCode;
import model.Model;
import model.request.UserUpdateRequest;
import util.extractor.CookieExtractor;
import model.http.HttpRequest;
import model.http.HttpResponse;
import model.http.sub.RequestMethod;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resolver.view.ModelAndView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Router
public class UserHttpHandler implements DynamicHttpHandler {
    private static final Logger log = LoggerFactory.getLogger(UserHttpHandler.class);

    private static final String COOKIE_HEADER_KEY = "Set-Cookie";
    private static final String SESSION_ID_KEY = "sid";
    private static final String COOKIE_HEADER_SUFFIX = "; Path=/";

    public UserHttpHandler() {}

    @Transactional
    @RequestMapping(method = RequestMethod.POST, path = "/user/create")
    public ModelAndView createUser(HttpResponse res, @Formdata User user) {

        if (user.getUserId().length() < 4 || user.getName().length() < 4 ||
                user.getPassword().length() < 4)
            throw new CustomException(ErrorCode.REGISTRATION_FIELD_ERROR);

        if (UserDao.findById(user.getUserId()).isPresent()) {
            String encodedMsg = URLEncoder.encode("입력하신 ID로 만들어진 계정이 이미 존재합니다.", StandardCharsets.UTF_8);
            res.addHeader(COOKIE_HEADER_KEY, "alertMessage=" + encodedMsg + "; Path=/");
            return new ModelAndView("redirect:/registration");
        }

        if (UserDao.findByName(user.getName()).isPresent()) {
            String encodedMsg = URLEncoder.encode("입력하신 닉네임으로 만들어진 계정이 이미 존재합니다.", StandardCharsets.UTF_8);
            res.addHeader(COOKIE_HEADER_KEY, "alertMessage=" + encodedMsg + "; Path=/");
            return new ModelAndView("redirect:/registration");
        }

        UserDao.create(user);

        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/login")
    public ModelAndView login(HttpResponse res, Model model, @Formdata User user) {

        Optional<User> findUser = UserDao.findById(user.getUserId());
        if (findUser.isEmpty()) {
            String encodedMsg = URLEncoder.encode("존재하지 않는 아이디입니다. 회원 가입 하시겠습니까?", StandardCharsets.UTF_8);
            String encodedUrl = URLEncoder.encode("/registration", StandardCharsets.UTF_8);
            res.addHeader(COOKIE_HEADER_KEY, "confirmMessage=" + encodedMsg + "; Path=/");
            res.addHeader(COOKIE_HEADER_KEY, "confirmUrl=" + encodedUrl + "; Path=/");

            return new ModelAndView(model, "redirect:/login");
        }

        if (!findUser.get().getPassword().equals(user.getPassword())) {
            String encodedMsg = URLEncoder.encode("비밀번호가 틀렸습니다.", StandardCharsets.UTF_8);
            res.addHeader(COOKIE_HEADER_KEY, "alertMessage=" + encodedMsg + "; Path=/");

            return new ModelAndView(model, "redirect:/login");
        }

        setSessionCookie(res, findUser.get());
        return new ModelAndView(model, "redirect:/");
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/logout")
    public ModelAndView logout(HttpRequest req, HttpResponse res) {
        expireSessionCookie(res);
        SessionStorage.removeSession(CookieExtractor.getValue(req, SESSION_ID_KEY));
        return new ModelAndView( "redirect:/");
    }

    @LoginRequired
    @RequestMapping(method = RequestMethod.POST, path = "/user/info")
    public ModelAndView updateUserInfo(HttpResponse res, @SessionUser User user,
                                       @MultipartFormdata UserUpdateRequest userUpdateReq) {

        // 1. 유효성 검사
        if (!userUpdateReq.getPassword().equals(userUpdateReq.getConfirmedPassword())) {
            throw new CustomException(ErrorCode.MISMATCHED_PASSWORD);
        }

        if (!userUpdateReq.getName().isEmpty()) {
            if (userUpdateReq.getName().length() < 4) {
                throw new CustomException(ErrorCode.REGISTRATION_FIELD_ERROR);
            }
            user.setName(userUpdateReq.getName());
        }

        if (!userUpdateReq.getPassword().isEmpty()) {
            if (userUpdateReq.getPassword().length() < 4) {
                throw new CustomException(ErrorCode.REGISTRATION_FIELD_ERROR);
            }
            user.setPassword(userUpdateReq.getPassword());
        }

        if ("true".equals(userUpdateReq.getDeleteProfileImage())) {
            user.setProfileImageUrl("");
        }

        if (userUpdateReq.getFile() != null) {
            userUpdateReq.getFile().saveFileAs(user.getUserId(), "profile/");

            String extension = userUpdateReq.getFile().getExtension();
            String imageUrl = "/profile/" + user.getUserId() + extension;
            user.setProfileImageUrl(imageUrl);
        }

        UserDao.editInfo(user);

        String encodedMsg = URLEncoder.encode("회원 정보가 정상적으로 업데이트되었습니다.", StandardCharsets.UTF_8);
        res.addHeader(COOKIE_HEADER_KEY, "alertMessage=" + encodedMsg + "; Path=/");

        return new ModelAndView("redirect:/mypage");
    }

    private void setSessionCookie(HttpResponse res, User user) {
        int randomSid = ThreadLocalRandom.current().nextInt(100000, 1000000);
        SessionStorage.addSession(String.valueOf(randomSid), user);
        res.addHeader(COOKIE_HEADER_KEY, SESSION_ID_KEY + "=" + randomSid + COOKIE_HEADER_SUFFIX);
    }

    private void expireSessionCookie(HttpResponse res) {
        res.addHeader(COOKIE_HEADER_KEY, "sid=; Path=/; Max-Age=0");
    }
}
