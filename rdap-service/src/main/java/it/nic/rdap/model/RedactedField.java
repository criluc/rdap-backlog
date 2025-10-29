package it.nic.rdap.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RedactedField(
        RedactionDescriptor name,
        String prePath,
        String path,
        String pathLang,
        String method,
        RedactionDescriptor reason,
        List<String> description
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RedactionDescriptor(
            String description,
            String type
    ) {
    }
}
