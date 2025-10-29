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
import it.nic.rdap.service.RedactionService;
import it.nic.rdap.service.RdapService;
import it.nic.rdap.web.ContactFormat;
import it.nic.rdap.web.ContactFormatResolver;
import it.nic.rdap.web.RdapContactAdapter;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping(path = "/rdap", produces = "application/rdap+json")
public class RdapController {

    private final RdapService rdapService;
    private final ContactFormatResolver contactFormatResolver;
    private final RdapContactAdapter rdapContactAdapter;
    private final RedactionService redactionService;

    public RdapController(RdapService rdapService,
                          ContactFormatResolver contactFormatResolver,
                          RdapContactAdapter rdapContactAdapter,
                          RedactionService redactionService) {
        this.rdapService = rdapService;
        this.contactFormatResolver = contactFormatResolver;
        this.rdapContactAdapter = rdapContactAdapter;
        this.redactionService = redactionService;
    }

    @GetMapping(path = "/domain/{name}")
    public DomainResource domainLookup(@PathVariable("name") String name, HttpServletRequest request) {
        ContactFormat format = contactFormatResolver.resolve(request);
        DomainResource resource = rdapService.domain(name);
        DomainResource adapted = rdapContactAdapter.adapt(resource, format);
        return redactionService.redact(adapted, format);
    }

    @GetMapping(path = "/nameserver/{ldhName}")
    public NameserverResource nameserverLookup(@PathVariable("ldhName") String ldhName, HttpServletRequest request) {
        ContactFormat format = contactFormatResolver.resolve(request);
        NameserverResource resource = rdapService.nameserver(ldhName);
        return rdapContactAdapter.adapt(resource, format);
    }

    @GetMapping(path = "/entity/{handle}")
    public RdapEntity entityLookup(@PathVariable("handle") String handle, HttpServletRequest request) {
        ContactFormat format = contactFormatResolver.resolve(request);
        RdapEntity entity = rdapService.entity(handle);
        RdapEntity adapted = rdapContactAdapter.adapt(entity, format);
        return redactionService.redact(adapted, format);
    }

    @GetMapping(path = "/ip/{address}/{prefix}")
    public IpNetworkResource ipLookup(@PathVariable("address") String address,
                                      @PathVariable("prefix") String prefix,
                                      HttpServletRequest request) {
        ContactFormat format = contactFormatResolver.resolve(request);
        IpNetworkResource resource = rdapService.network(address + "/" + prefix);
        return rdapContactAdapter.adapt(resource, format);
    }

    @GetMapping(path = "/autnum/{asn}")
    public AutnumResource autnumLookup(@PathVariable("asn") long asn, HttpServletRequest request) {
        ContactFormat format = contactFormatResolver.resolve(request);
        AutnumResource resource = rdapService.autnum(asn);
        return rdapContactAdapter.adapt(resource, format);
    }

    @GetMapping(path = "/help")
    public HelpResponse help() {
        return rdapService.help();
    }

    @GetMapping(path = "/domains")
    public DomainSearchResponse domainSearch(@RequestParam("name") @NotBlank String name, HttpServletRequest request) {
        ContactFormat format = contactFormatResolver.resolve(request);
        DomainSearchResponse response = rdapService.searchDomains(name);
        DomainSearchResponse adapted = rdapContactAdapter.adapt(response, format);
        return redactionService.redact(adapted, format);
    }

    @GetMapping(path = "/nameservers")
    public NameserverSearchResponse nameserverSearch(@RequestParam("name") @NotBlank String name, HttpServletRequest request) {
        ContactFormat format = contactFormatResolver.resolve(request);
        NameserverSearchResponse response = rdapService.searchNameservers(name);
        return rdapContactAdapter.adapt(response, format);
    }

    @GetMapping(path = "/entities")
    public EntitySearchResponse entitySearch(
            @RequestParam(value = "handle", required = false) String handle,
            @RequestParam(value = "fn", required = false) String fn,
            HttpServletRequest request) {
        String query = pickFirstNonBlank(handle, fn);
        if (query == null) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    "Parametro handle o fn richiesto per la ricerca entity"
            );
        }
        ContactFormat format = contactFormatResolver.resolve(request);
        EntitySearchResponse response = rdapService.searchEntities(query);
        EntitySearchResponse adapted = rdapContactAdapter.adapt(response, format);
        return redactionService.redact(adapted, format);
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
