package it.nic.rdap.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ContactFormatResolver {

    private static final String FORMAT_PARAM = "contactFormat";
    private static final String FORMAT_HEADER = "X-RDAP-Contact-Format";

    public ContactFormat resolve(HttpServletRequest request) {
        String explicit = request.getParameter(FORMAT_PARAM);
        if (StringUtils.hasText(explicit) && explicit.equalsIgnoreCase("jscontact")) {
            return ContactFormat.JSCONTACT;
        }

        String headerOverride = request.getHeader(FORMAT_HEADER);
        if (StringUtils.hasText(headerOverride) && headerOverride.equalsIgnoreCase("jscontact")) {
            return ContactFormat.JSCONTACT;
        }

        String accept = request.getHeader("Accept");
        if (StringUtils.hasText(accept) && accept.toLowerCase().contains("jscontact")) {
            return ContactFormat.JSCONTACT;
        }

        return ContactFormat.JCARD;
    }
}
