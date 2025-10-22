package it.nic.rdap.web;

import it.nic.rdap.model.AutnumResource;
import it.nic.rdap.model.DomainResource;
import it.nic.rdap.model.DomainSearchResponse;
import it.nic.rdap.model.EntitySearchResponse;
import it.nic.rdap.model.IpNetworkResource;
import it.nic.rdap.model.NameserverResource;
import it.nic.rdap.model.NameserverSearchResponse;
import it.nic.rdap.model.RdapEntity;
import it.nic.rdap.service.RdapDataStore;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RdapContactAdapter {

    public DomainResource adapt(DomainResource resource, ContactFormat format) {
        List<RdapEntity> adaptedEntities = resource.entities() == null
                ? null
                : resource.entities().stream()
                .map(entity -> adapt(entity, format))
                .collect(Collectors.toCollection(ArrayList::new));

        return new DomainResource(
                conformance(resource.rdapConformance(), format),
                resource.objectClassName(),
                resource.handle(),
                resource.ldhName(),
                resource.unicodeName(),
                resource.status(),
                resource.events(),
                resource.notices(),
                adaptedEntities,
                resource.links()
        );
    }

    public RdapEntity adapt(RdapEntity entity, ContactFormat format) {
        Object vcard = format == ContactFormat.JSCONTACT ? null : entity.vcardArray();
        var jsContact = format == ContactFormat.JSCONTACT ? entity.jsContactCard() : null;
        return new RdapEntity(
                conformance(entity.rdapConformance(), format),
                entity.objectClassName(),
                entity.handle(),
                entity.roles(),
                vcard,
                jsContact,
                entity.publicIds(),
                entity.links(),
                entity.events()
        );
    }

    public DomainSearchResponse adapt(DomainSearchResponse response, ContactFormat format) {
        List<DomainResource> adapted = response.domainSearchResults()
                .stream()
                .map(domainResource -> adapt(domainResource, format))
                .toList();
        return new DomainSearchResponse(
                conformance(response.rdapConformance(), format),
                adapted,
                response.notices(),
                response.links()
        );
    }

    public EntitySearchResponse adapt(EntitySearchResponse response, ContactFormat format) {
        List<RdapEntity> adapted = response.entitySearchResults()
                .stream()
                .map(entity -> adapt(entity, format))
                .toList();
        return new EntitySearchResponse(
                conformance(response.rdapConformance(), format),
                adapted,
                response.notices(),
                response.links()
        );
    }

    public NameserverSearchResponse adapt(NameserverSearchResponse response, ContactFormat format) {
        return new NameserverSearchResponse(
                conformance(response.rdapConformance(), format),
                response.nameserverSearchResults(),
                response.notices(),
                response.links()
        );
    }

    public NameserverResource adapt(NameserverResource resource, ContactFormat format) {
        return new NameserverResource(
                conformance(resource.rdapConformance(), format),
                resource.objectClassName(),
                resource.handle(),
                resource.ldhName(),
                resource.unicodeName(),
                resource.status(),
                resource.ipAddresses(),
                resource.events(),
                resource.notices(),
                resource.links()
        );
    }

    public AutnumResource adapt(AutnumResource resource, ContactFormat format) {
        List<RdapEntity> adaptedEntities = resource.entities() == null
                ? null
                : resource.entities().stream()
                .map(entity -> adapt(entity, format))
                .toList();
        return new AutnumResource(
                conformance(resource.rdapConformance(), format),
                resource.objectClassName(),
                resource.handle(),
                resource.startAutnum(),
                resource.endAutnum(),
                resource.name(),
                resource.type(),
                resource.status(),
                resource.events(),
                resource.notices(),
                adaptedEntities,
                resource.links()
        );
    }

    public IpNetworkResource adapt(IpNetworkResource resource, ContactFormat format) {
        List<RdapEntity> adaptedEntities = resource.entities() == null
                ? null
                : resource.entities().stream()
                .map(entity -> adapt(entity, format))
                .toList();
        return new IpNetworkResource(
                conformance(resource.rdapConformance(), format),
                resource.objectClassName(),
                resource.handle(),
                resource.startAddress(),
                resource.endAddress(),
                resource.ipVersion(),
                resource.name(),
                resource.type(),
                resource.country(),
                resource.status(),
                resource.events(),
                resource.notices(),
                adaptedEntities,
                resource.links()
        );
    }

    private List<String> conformance(List<String> base, ContactFormat format) {
        if (format != ContactFormat.JSCONTACT) {
            if (base.contains(RdapDataStore.JSCONTACT_CONFORMANCE)) {
                List<String> trimmed = new ArrayList<>(base);
                trimmed.remove(RdapDataStore.JSCONTACT_CONFORMANCE);
                return List.copyOf(trimmed);
            }
            return base;
        }
        if (base.contains(RdapDataStore.JSCONTACT_CONFORMANCE)) {
            return base;
        }
        List<String> updated = new ArrayList<>(base);
        updated.add(RdapDataStore.JSCONTACT_CONFORMANCE);
        return List.copyOf(updated);
    }
}
