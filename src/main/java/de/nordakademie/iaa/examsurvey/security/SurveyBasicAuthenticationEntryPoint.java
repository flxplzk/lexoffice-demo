package de.nordakademie.iaa.examsurvey.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Custom EntryPoint to Customize Realm and response
 *
 * @author Felix Plazek
 */
public class SurveyBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    private final String realm;

    public SurveyBasicAuthenticationEntryPoint(String realm) {
        this.realm = realm;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // dark hack to prevent browser authentication dialog ... sorry
        response.addHeader("WWW-Authenticate", "xBasic realm=" + getRealmName() + "");

        PrintWriter writer = response.getWriter();
        writer.println("HTTP Status 401 : " + authEx.getMessage());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName(realm);
        super.afterPropertiesSet();
    }
}