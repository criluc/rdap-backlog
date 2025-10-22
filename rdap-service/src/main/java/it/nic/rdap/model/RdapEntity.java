package it.nic.rdap.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RdapEntity(
        String objectClassName,
        String handle,
        List<String> roles,
        Object vcardArray,
        List<RdapPublicId> publicIds,
        List<RdapLink> links,
        List<RdapEvent> events
) {
}
