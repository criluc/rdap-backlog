package it.nic.rdap.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RdapSecureDns(
        Boolean delegationSigned,
        Boolean zoneSigned,
        Integer maxSigLife
) {
}
