package com.ventyx.security.admin.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.ventyx.security.admin.Constants;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Interceptor protecting all secured resources
 */
public class SecurityInterceptor implements Interceptor, StrutsStatics, SessionAware, ParameterAware {

    private Map<String, String[]> parameterMap;
    private Map<String, Object> sessionMap;

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {

        final ActionContext context = actionInvocation.getInvocationContext ();
        HttpServletRequest request = (HttpServletRequest) context.get(HTTP_REQUEST);
        HttpSession session =  request.getSession (true);

        // Is there a "user" object stored in the user's HttpSession?
        Object user = session.getAttribute (Constants.USER_HANDLE);
        if (user == null) {
            // The user has not logged in yet.

            // Is the user attempting to log in right now?
            String loginAttempt = request.getParameter (Constants.LOGIN_ATTEMPT);
            if (loginAttempt != null && !loginAttempt.isEmpty()) { // The user is attempting to log in.

                // Process the user's login attempt.
                if (1 == 1) {
                    // The login succeeded send them the login-success page.
                    session.setAttribute(Constants.USER_HANDLE, "user");
                    return "login-success";
                } else {
                    // The login failed. Set an error if we can on the action.
                    Object action = actionInvocation.getAction ();
                    if (action instanceof ValidationAware) {
                        ((ValidationAware) action).addActionError ("Username or password incorrect.");
                    }
                }
            }

            // Either the login attempt failed or the user hasn't tried to login yet,
            // and we need to send the login form.
            return "login";
        } else {
            return actionInvocation.invoke ();
        }
    }

    @Override
    public void destroy() {
        return;
    }

    @Override
    public void init() {
        return;
    }

    @Override
    public void setParameters(Map<String, String[]> parameterMap) {
        this.parameterMap = parameterMap;
    }

    @Override
    public void setSession(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }

    public String getParameterValue(String key) {

        if(parameterMap == null || parameterMap.isEmpty()) {
            return null;
        }

        String[] parameterArray = parameterMap.get(key);
        return parameterArray[0];
    }

    public Object getSessionValue(String key) {

        if(sessionMap == null || sessionMap.isEmpty()) {
            return null;
        }
        return sessionMap.get(key);
    }
}
