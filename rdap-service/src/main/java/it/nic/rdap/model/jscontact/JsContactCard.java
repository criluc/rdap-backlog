package it.nic.rdap.model.jscontact;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JsContactCard(
        String uid,
        String kind,
        JsContactName name,
        Map<String, JsContactEmail> emails,
        Map<String, JsContactPhone> phones,
        Map<String, JsContactAddress> addresses,
        Map<String, JsContactOrganisation> organizations,
        List<String> roles
) {
}
