package it.nic.rdap.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DomainSearchResponse(
        List<String> rdapConformance,
        List<RedactedField> redacted,
        List<DomainResource> domainSearchResults,
        List<RdapNotice> notices,
        List<RdapLink> links
) {
}
