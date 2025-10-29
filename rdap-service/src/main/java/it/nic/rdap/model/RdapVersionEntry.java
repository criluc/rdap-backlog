package it.nic.rdap.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RdapVersionEntry(
        String extension,
        String type,
        @JsonProperty("version")
        String version
) {
}
