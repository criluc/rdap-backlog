package it.nic.rdap.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RedactedField(
        String name,
        String path,
        String prePath,
        String method,
        List<String> description
) {
}
