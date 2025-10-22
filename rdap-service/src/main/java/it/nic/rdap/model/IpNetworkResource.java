package it.nic.rdap.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record IpNetworkResource(
        List<String> rdapConformance,
        String objectClassName,
        String handle,
        String startAddress,
        String endAddress,
        String ipVersion,
        String name,
        String type,
        String country,
        List<String> status,
        List<RdapEvent> events,
        List<RdapNotice> notices,
        List<RdapEntity> entities,
        List<RdapLink> links
) {
}
