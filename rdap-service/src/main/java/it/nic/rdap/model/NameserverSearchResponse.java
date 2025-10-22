package it.nic.rdap.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NameserverSearchResponse(
        List<String> rdapConformance,
        List<NameserverResource> nameserverSearchResults,
        List<RdapNotice> notices,
        List<RdapLink> links
) {
}
