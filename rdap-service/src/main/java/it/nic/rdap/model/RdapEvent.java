package it.nic.rdap.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RdapEvent(
        String eventAction,
        String eventDate
) {
}
