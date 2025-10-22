package it.nic.rdap.service;

import it.nic.rdap.model.AutnumResource;
import it.nic.rdap.model.DomainResource;
import it.nic.rdap.model.DomainSearchResponse;
import it.nic.rdap.model.EntitySearchResponse;
import it.nic.rdap.model.HelpResponse;
import it.nic.rdap.model.IpNetworkResource;
import it.nic.rdap.model.NameserverResource;
import it.nic.rdap.model.NameserverSearchResponse;
import it.nic.rdap.model.RdapEntity;
import org.springframework.stereotype.Service;

@Service
public class RdapService {

    private final RdapDataStore dataStore;

    public RdapService(RdapDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public DomainResource domain(String name) {
        return dataStore.requireDomain(name);
    }

    public NameserverResource nameserver(String ldhName) {
        return dataStore.requireNameserver(ldhName);
    }

    public RdapEntity entity(String handle) {
        return dataStore.requireEntity(handle);
    }

    public IpNetworkResource network(String cidr) {
        return dataStore.requireNetwork(cidr);
    }

    public AutnumResource autnum(long asn) {
        return dataStore.requireAutnum(asn);
    }

    public DomainSearchResponse searchDomains(String query) {
        return dataStore.searchDomains(query);
    }

    public NameserverSearchResponse searchNameservers(String query) {
        return dataStore.searchNameservers(query);
    }

    public EntitySearchResponse searchEntities(String query) {
        return dataStore.searchEntities(query);
    }

    public HelpResponse help() {
        return dataStore.help();
    }
}
