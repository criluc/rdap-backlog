package it.nic.rdap.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RdapErrorResponse(
        int errorCode,
        String title,
        List<String> description,
        List<RdapLink> links
) {
}
