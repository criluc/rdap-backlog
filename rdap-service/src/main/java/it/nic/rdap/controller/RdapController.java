package it.nic.rdap.controller;

import it.nic.rdap.model.AutnumResource;
import it.nic.rdap.model.DomainResource;
import it.nic.rdap.model.DomainSearchResponse;
import it.nic.rdap.model.EntitySearchResponse;
import it.nic.rdap.model.HelpResponse;
import it.nic.rdap.model.IpNetworkResource;
import it.nic.rdap.model.NameserverResource;
import it.nic.rdap.model.NameserverSearchResponse;
import it.nic.rdap.model.RdapEntity;
import it.nic.rdap.service.RdapService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Validated
@RestController
@RequestMapping(produces = "application/rdap+json")
public class RdapController {

    private final RdapService rdapService;

    public RdapController(RdapService rdapService) {
        this.rdapService = rdapService;
    }

    @GetMapping(path = "/domain/{name}")
    public DomainResource domainLookup(@PathVariable("name") String name) {
        return rdapService.domain(name);
    }

    @GetMapping(path = "/nameserver/{ldhName}")
    public NameserverResource nameserverLookup(@PathVariable("ldhName") String ldhName) {
        return rdapService.nameserver(ldhName);
    }

    @GetMapping(path = "/entity/{handle}")
    public RdapEntity entityLookup(@PathVariable("handle") String handle) {
        return rdapService.entity(handle);
    }

    @GetMapping(path = "/ip/{cidr:.+}")
    public IpNetworkResource ipLookup(@PathVariable("cidr") String cidr) {
        return rdapService.network(cidr);
    }

    @GetMapping(path = "/autnum/{asn}")
    public AutnumResource autnumLookup(@PathVariable("asn") long asn) {
        return rdapService.autnum(asn);
    }

    @GetMapping(path = "/help")
    public HelpResponse help() {
        return rdapService.help();
    }

    @GetMapping(path = "/domains")
    public DomainSearchResponse domainSearch(@RequestParam("name") @NotBlank String name) {
        return rdapService.searchDomains(name);
    }

    @GetMapping(path = "/nameservers")
    public NameserverSearchResponse nameserverSearch(@RequestParam("name") @NotBlank String name) {
        return rdapService.searchNameservers(name);
    }

    @GetMapping(path = "/entities")
    public EntitySearchResponse entitySearch(
            @RequestParam(value = "handle", required = false) String handle,
            @RequestParam(value = "fn", required = false) String fn) {
        String query = pickFirstNonBlank(handle, fn);
        if (query == null) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Parametro handle o fn richiesto per la ricerca entity"
            );
        }
        return rdapService.searchEntities(query);
    }

    private String pickFirstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }
}
