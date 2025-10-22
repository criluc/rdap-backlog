package it.nic.rdap.model.jscontact;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JsContactOrganisation(
        String name
) {
}
