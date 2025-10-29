package it.nic.rdap.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.nic.rdap.model.jscontact.JsContactCard;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RdapEntity(
        List<String> rdapConformance,
        List<RedactedField> redacted,
        String objectClassName,
        String handle,
        List<String> roles,
        Object vcardArray,
        JsContactCard jsContactCard,
        List<RdapPublicId> publicIds,
        List<RdapLink> links,
        List<RdapEvent> events
) {
}
