package it.nic.rdap.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AutnumResource(
        String objectClassName,
        String handle,
        Long startAutnum,
        Long endAutnum,
        String name,
        String type,
        List<String> status,
        List<RdapEvent> events,
        List<RdapNotice> notices,
        List<RdapEntity> entities,
        List<RdapLink> links
) {
}
