package it.nic.rdap.model.jscontact;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JsContactEmail(
        String value,
        List<String> contexts,
        Boolean preferred
) {
}
