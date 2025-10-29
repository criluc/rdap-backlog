package it.nic.rdap.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RdapDomainNameserver(
        String objectClassName,
        String ldhName,
        String unicodeName,
        String handle
) {
}
