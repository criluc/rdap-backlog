package it.nic.rdap.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NameserverResource(
        String objectClassName,
        String handle,
        String ldhName,
        String unicodeName,
        List<String> status,
        List<String> ipAddresses,
        List<RdapEvent> events,
        List<RdapNotice> notices,
        List<RdapLink> links
) {
}
