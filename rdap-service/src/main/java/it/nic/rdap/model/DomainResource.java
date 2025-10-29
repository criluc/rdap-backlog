package it.nic.rdap.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DomainResource(
        List<String> rdapConformance,
        List<RedactedField> redacted,
        String objectClassName,
        String handle,
        String ldhName,
        String unicodeName,
        List<String> status,
        List<RdapEvent> events,
        List<RdapNotice> notices,
        List<RdapEntity> entities,
        List<RdapLink> links,
        List<RdapDomainNameserver> nameservers,
        RdapSecureDns secureDNS,
        @JsonProperty("versioning_data") List<RdapVersionEntry> versioningData
) {
}
