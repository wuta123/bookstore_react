package com.bookstore.www.utils;
import com.bookstore.www.entity.Userinfo;
import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.apache.catalina.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
@Scope("session")
public class SessionUtils {
    private static final String USER_NAME_IN_SESSION = "username";

    public static HttpSession getSession() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest().getSession();
    }

    public static Userinfo getUserFromSession() {
        HttpSession session = getSession();
        return (Userinfo) session.getAttribute(USER_NAME_IN_SESSION);
    }

    public static Userinfo updateUserInSession(Userinfo userinfo) {
        HttpSession session = getSession();
        session.setAttribute(USER_NAME_IN_SESSION, userinfo);
        return getUserFromSession();
    }

    public static Userinfo putUserIntoSession(Userinfo userinfo) {
        HttpSession session = getSession();
        session.setAttribute(USER_NAME_IN_SESSION, userinfo);
        return getUserFromSession();
    }

    public static Userinfo removeUserFromSession() {
        HttpSession session = getSession();
        Userinfo user = getUserFromSession();
        session.removeAttribute(USER_NAME_IN_SESSION);
        return user;
    }
}
