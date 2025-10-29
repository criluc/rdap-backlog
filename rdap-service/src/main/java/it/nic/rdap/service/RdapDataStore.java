package it.nic.rdap.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.nic.rdap.model.AutnumResource;
import it.nic.rdap.model.DomainResource;
import it.nic.rdap.model.DomainSearchResponse;
import it.nic.rdap.model.EntitySearchResponse;
import it.nic.rdap.model.HelpResponse;
import it.nic.rdap.model.IpNetworkResource;
import it.nic.rdap.model.NameserverResource;
import it.nic.rdap.model.NameserverSearchResponse;
import it.nic.rdap.model.RdapEntity;
import it.nic.rdap.model.RdapEvent;
import it.nic.rdap.model.RdapLink;
import it.nic.rdap.model.RdapNotice;
import it.nic.rdap.model.RdapPublicId;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Map.entry;

@Component
public class RdapDataStore {

    private static final String BASE_URL = "https://rdap.nic.it/rdap";
    private static final DateTimeFormatter EVENT_FORMATTER = DateTimeFormatter.ISO_DATE;
    private static final List<String> BASE_RDAP_CONFORMANCE = List.of("rdap_level_0");
    public static final String JSCONTACT_CONFORMANCE = "draft-ietf-regext-rdap-jscontact";

    private final Map<String, DomainResource> domains;
    private final Map<String, NameserverResource> nameservers;
    private final Map<String, RdapEntity> entities;
    private final Map<Long, AutnumResource> autnums;
    private final Map<String, IpNetworkResource> networks;
    private final HelpResponse helpResponse;
    private final JsContactMapper jsContactMapper;
    private final ObjectMapper objectMapper;

    public RdapDataStore(JsContactMapper jsContactMapper, ObjectMapper objectMapper) {
        this.jsContactMapper = jsContactMapper;
        this.objectMapper = objectMapper;
        RdapEntity registryEntity = buildRegistryEntity();
        RdapEntity registrarEntity = buildRegistrarEntity();
        RdapEntity registrantEntity = buildContactEntity("SH8013-REGISTRANT", "Registrant Example", "registrant@example.it");

        entities = Map.ofEntries(
                entry(registryEntity.handle().toLowerCase(Locale.ROOT), registryEntity),
                entry(registrarEntity.handle().toLowerCase(Locale.ROOT), registrarEntity),
                entry(registrantEntity.handle().toLowerCase(Locale.ROOT), registrantEntity)
        );

        domains = Map.ofEntries(
                entry("example.it", buildDomain("EXAMPLE-IT", "example.it", registryEntity, registrarEntity, registrantEntity)),
                entry("nic.it", loadDomainResource("data/domain-nic-it.json"))
        );

        nameservers = Map.ofEntries(
                entry("ns1.nic.it", buildNameserver("NS1-NIC", "ns1.nic.it", List.of("192.0.2.53"))),
                entry("ns2.nic.it", buildNameserver("NS2-NIC", "ns2.nic.it", List.of("198.51.100.53", "2001:db8::53")))
        );

        autnums = Map.ofEntries(
                entry(196672L, buildAutnum(196672L, "Example Network", registryEntity, registrarEntity))
        );

        networks = Map.ofEntries(
                entry("192.0.2.0/24", buildIpv4Network("NET-192-0-2-0-24", "192.0.2.0", "192.0.2.255", "Example IPv4 Network", registryEntity)),
                entry("2001:db8::/32", buildIpv6Network("NET-2001-DB8", "2001:db8::", "2001:db8:0:ffff:ffff:ffff:ffff:ffff", "Example IPv6 Network", registryEntity))
        );

        helpResponse = new HelpResponse(
                "Italian RDAP Service",
                List.of(
                        "Benvenuto nel servizio RDAP dimostrativo.",
                        "Gli endpoint implementano una porzione minima di RFC 9082/9083.",
                        "Le entity possono essere fornite sia in jCard (RFC 7095) sia in JSContact (RFC 9553 / draft-ietf-regext-rdap-jscontact).",
                        "Richiedi JSContact impostando l'header Accept: application/rdap+json;ext=jscontact o il parametro contactFormat=jscontact."
                ),
                List.of(new RdapLink("service", BASE_URL + "/help", "application/rdap+json", null)),
                List.of(
                        new RdapNotice(
                                "Policy",
                                List.of("L'uso del servizio è soggetto a limiti di query."),
                                List.of(new RdapLink("about", "https://www.nic.it", "text/html", null))
                        ),
                        new RdapNotice(
                                "JSContact Transition",
                                List.of(
                                        "Esempio richiesta: curl -H 'Accept: application/rdap+json;ext=jscontact' " + BASE_URL + "/entity/NIC-REG",
                                        "Il server continuerà a fornire jCard in assenza dell'estensione e segnala la compatibilità tramite rdapConformance."
                                ),
                                List.of(
                                        new RdapLink("specification", "https://datatracker.ietf.org/doc/draft-ietf-regext-rdap-jscontact/", "text/html", null)
                                )
                        )
                )
        );
    }

    public Optional<DomainResource> findDomain(String name) {
        return Optional.ofNullable(domains.get(normalizeKey(name)));
    }

    public Optional<NameserverResource> findNameserver(String ldhName) {
        return Optional.ofNullable(nameservers.get(normalizeKey(ldhName)));
    }

    public Optional<RdapEntity> findEntity(String handle) {
        return Optional.ofNullable(entities.get(normalizeKey(handle)));
    }

    public Optional<AutnumResource> findAutnum(long asn) {
        return Optional.ofNullable(autnums.get(asn));
    }

    public Optional<IpNetworkResource> findNetwork(String cidr) {
        return Optional.ofNullable(networks.get(normalizeKey(cidr)));
    }

    public DomainSearchResponse searchDomains(String query) {
        String normalized = normalizeKey(query);
        List<DomainResource> results = filter(domains, entry -> {
            String key = entry.getKey();
            String ldhName = entry.getValue().ldhName().toLowerCase(Locale.ROOT);
            return key.contains(normalized) || ldhName.contains(normalized);
        });
        return new DomainSearchResponse(BASE_RDAP_CONFORMANCE, null, results, defaultNotices(), defaultLinks("/domains?name=" + query));
    }

    public NameserverSearchResponse searchNameservers(String query) {
        List<NameserverResource> results = filter(nameservers, entry ->
                entry.getKey().contains(normalizeKey(query)));
        return new NameserverSearchResponse(BASE_RDAP_CONFORMANCE, results, defaultNotices(), defaultLinks("/nameservers?name=" + query));
    }

    public EntitySearchResponse searchEntities(String query) {
        List<RdapEntity> results = filter(entities, entry ->
                entry.getKey().contains(normalizeKey(query)));
        return new EntitySearchResponse(BASE_RDAP_CONFORMANCE, null, results, defaultNotices(), defaultLinks("/entities?fn=" + query));
    }

    public HelpResponse help() {
        return helpResponse;
    }

    private String normalizeKey(String value) {
        return value == null ? null : value.toLowerCase(Locale.ROOT);
    }

    private RdapEntity buildRegistryEntity() {
        List<String> roles = List.of("registrar", "registry");
        String handle = "NIC-REG";
        Object vcard = buildVcard("NIC Registry", "rdap-support@nic.it", roles);
        return new RdapEntity(
                BASE_RDAP_CONFORMANCE,
                null,
                "entity",
                handle,
                roles,
                vcard,
                jsContactMapper.fromRdapEntity(handle, vcard, roles),
                List.of(new RdapPublicId("IANA", "1234")),
                defaultLinks("/entity/NIC-REG"),
                List.of(new RdapEvent("last changed", EVENT_FORMATTER.format(LocalDate.of(2024, 6, 18)))),
                null,
                null,
                null
        );
    }

    private RdapEntity buildRegistrarEntity() {
        List<String> roles = List.of("registrar");
        String handle = "REG-EXAMPLE";
        Object vcard = buildVcard("Example Registrar S.p.A.", "contact@example-registrar.it", roles);
        return new RdapEntity(
                BASE_RDAP_CONFORMANCE,
                null,
                "entity",
                handle,
                roles,
                vcard,
                jsContactMapper.fromRdapEntity(handle, vcard, roles),
                List.of(new RdapPublicId("IANA Registrar ID", "9999")),
                defaultLinks("/entity/REG-EXAMPLE"),
                List.of(new RdapEvent("last changed", EVENT_FORMATTER.format(LocalDate.of(2025, 1, 15)))),
                null,
                null,
                null
        );
    }

    private RdapEntity buildContactEntity(String handle, String name, String email) {
        List<String> roles = List.of("registrant");
        Object vcard = buildVcard(name, email, roles);
        return new RdapEntity(
                BASE_RDAP_CONFORMANCE,
                null,
                "entity",
                handle,
                roles,
                vcard,
                jsContactMapper.fromRdapEntity(handle, vcard, roles),
                List.of(),
                defaultLinks("/entity/" + handle),
                List.of(new RdapEvent("last changed", EVENT_FORMATTER.format(LocalDate.of(2024, 12, 10)))),
                List.of("active"),
                null,
                null
        );
    }

    private DomainResource buildDomain(String handle, String ldhName, RdapEntity registryEntity, RdapEntity registrarEntity, RdapEntity registrantEntity) {
        List<RdapEntity> assocEntities = List.of(
                registryEntity,
                registrarEntity,
                registrantEntity
        );
        return new DomainResource(
                BASE_RDAP_CONFORMANCE,
                null,
                "domain",
                handle,
                ldhName,
                ldhName,
                List.of("active"),
                List.of(
                        new RdapEvent("registration", EVENT_FORMATTER.format(LocalDate.of(2010, 5, 1))),
                        new RdapEvent("expiration", EVENT_FORMATTER.format(LocalDate.of(2030, 5, 1)))
                ),
                defaultNotices(),
                assocEntities,
                defaultLinks("/domain/" + ldhName),
                null,
                null,
                null
        );
    }

    private DomainResource loadDomainResource(String resourcePath) {
        Resource resource = new ClassPathResource(resourcePath);
        try (var inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, DomainResource.class);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load RDAP domain resource from " + resourcePath, e);
        }
    }

    private NameserverResource buildNameserver(String handle, String ldhName, List<String> ipAddresses) {
        return new NameserverResource(
                BASE_RDAP_CONFORMANCE,
                "nameserver",
                handle,
                ldhName,
                ldhName,
                List.of("active"),
                ipAddresses,
                List.of(new RdapEvent("last changed", EVENT_FORMATTER.format(LocalDate.of(2024, 11, 3)))),
                defaultNotices(),
                defaultLinks("/nameserver/" + ldhName)
        );
    }

    private AutnumResource buildAutnum(Long asn, String name, RdapEntity registryEntity, RdapEntity registrarEntity) {
        return new AutnumResource(
                BASE_RDAP_CONFORMANCE,
                "autnum",
                "AS" + asn,
                asn,
                asn,
                name,
                "DIRECT ALLOCATION",
                List.of("active"),
                List.of(new RdapEvent("registration", EVENT_FORMATTER.format(LocalDate.of(2012, 3, 14)))),
                defaultNotices(),
                List.of(registryEntity, registrarEntity),
                defaultLinks("/autnum/" + asn)
        );
    }

    private IpNetworkResource buildIpv4Network(String handle, String start, String end, String name, RdapEntity entity) {
        return new IpNetworkResource(
                BASE_RDAP_CONFORMANCE,
                "ip network",
                handle,
                start,
                end,
                "v4",
                name,
                "ASSIGNED PA",
                "IT",
                List.of("active"),
                List.of(new RdapEvent("registration", EVENT_FORMATTER.format(LocalDate.of(2015, 8, 24)))),
                defaultNotices(),
                List.of(entity),
                defaultLinks("/ip/" + start + "/24")
        );
    }

    private IpNetworkResource buildIpv6Network(String handle, String start, String end, String name, RdapEntity entity) {
        return new IpNetworkResource(
                BASE_RDAP_CONFORMANCE,
                "ip network",
                handle,
                start,
                end,
                "v6",
                name,
                "ASSIGNED",
                "IT",
                List.of("active"),
                List.of(new RdapEvent("registration", EVENT_FORMATTER.format(LocalDate.of(2018, 1, 12)))),
                defaultNotices(),
                List.of(entity),
                defaultLinks("/ip/" + start + "/32")
        );
    }

    private Object buildVcard(String name, String email, List<String> roles) {
        List<Object> properties = new ArrayList<>();
        properties.add(List.of("fn", Map.of(), "text", name));

        List<Object> nValue = List.of("", name, "", "", "");
        properties.add(List.of("n", Map.of(), "text", nValue));

        Map<String, Object> emailParams = Map.of("type", List.of("work"), "pref", 1);
        properties.add(List.of("email", emailParams, "text", email));

        if (roles != null) {
            roles.forEach(role ->
                    properties.add(List.of("role", Map.of(), "text", role))
            );
        }

        return List.of("vcard", properties);
    }

    private List<RdapNotice> defaultNotices() {
        return List.of(
                new RdapNotice(
                        "Terms of Service",
                        List.of("Data forniti solo per uso informativo."),
                        List.of(new RdapLink("terms-of-service", "https://www.nic.it/en/terms", "text/html", null))
                )
        );
    }

    private List<RdapLink> defaultLinks(String path) {
        return List.of(
                new RdapLink("self", BASE_URL + path, "application/rdap+json", null)
        );
    }

    private <T> List<T> filter(Map<String, T> source, Predicate<Map.Entry<String, T>> predicate) {
        return source.entrySet()
                .stream()
                .filter(predicate)
                .map(Map.Entry::getValue)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public NameserverResource requireNameserver(String ldhName) {
        return findNameserver(ldhName)
                .orElseThrow(() -> new RdapNotFoundException("Nameserver non trovato: " + ldhName));
    }

    public RdapEntity requireEntity(String handle) {
        return findEntity(handle)
                .orElseThrow(() -> new RdapNotFoundException("Entity non trovata: " + handle));
    }

    public DomainResource requireDomain(String name) {
        return findDomain(name)
                .orElseThrow(() -> new RdapNotFoundException("Dominio non trovato: " + name));
    }

    public AutnumResource requireAutnum(long asn) {
        return findAutnum(asn)
                .orElseThrow(() -> new RdapNotFoundException("Autnum non trovato: AS" + asn));
    }

    public IpNetworkResource requireNetwork(String cidr) {
        return findNetwork(cidr)
                .orElseThrow(() -> new RdapNotFoundException("Rete non trovata: " + cidr));
    }
}
