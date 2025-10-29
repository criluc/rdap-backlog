package it.nic.rdap.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EntitySearchResponse(
        List<String> rdapConformance,
        List<RedactedField> redacted,
        List<RdapEntity> entitySearchResults,
        List<RdapNotice> notices,
        List<RdapLink> links
) {
}
