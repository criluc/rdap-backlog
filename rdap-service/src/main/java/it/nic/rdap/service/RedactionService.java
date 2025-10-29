package it.nic.rdap.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import it.nic.rdap.model.DomainResource;
import it.nic.rdap.model.DomainSearchResponse;
import it.nic.rdap.model.RedactedField;
import it.nic.rdap.model.RdapEntity;
import it.nic.rdap.web.ContactFormat;
import it.nic.rdap.model.EntitySearchResponse;
import it.nic.rdap.model.jscontact.JsContactCard;
import it.nic.rdap.model.jscontact.JsContactEmail;
import it.nic.rdap.model.jscontact.JsContactName;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
public class RedactionService {

    private static final String REDACTED_CONFORMANCE = "redacted";
    private static final String REGISTRANT_HANDLE = "SH8013-REGISTRANT";
    private static final String JSON_PATH_LANG = "jsonpath";
    private static final RedactedField.RedactionDescriptor DEFAULT_REASON =
            new RedactedField.RedactionDescriptor("Server policy", null);

    private final ObjectMapper objectMapper;
    private final Configuration jsonPathConfiguration;

    public RedactionService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jsonPathConfiguration = Configuration.builder()
                .jsonProvider(new JacksonJsonNodeJsonProvider())
                .mappingProvider(new JacksonMappingProvider())
                .build();
    }

    public DomainResource redact(DomainResource resource, ContactFormat format) {
        if (resource == null) {
            return null;
        }
        List<RdapEntity> entities = resource.entities();
        if (entities == null || entities.isEmpty()) {
            return resource;
        }
        JsonNode originalNode = objectMapper.valueToTree(resource);
        List<RdapEntity> redactedEntities = new ArrayList<>(entities.size());
        List<RedactedField> combinedRedactions = resource.redacted() == null
                ? new ArrayList<>()
                : new ArrayList<>(resource.redacted());
        int originalRedactionCount = combinedRedactions.size();

        for (int index = 0; index < entities.size(); index++) {
            RdapEntity entity = entities.get(index);
            JsonNode entityNode = originalNode.path("entities").get(index);
            RedactionOutcome<RdapEntity> outcome = redactEntityInternal(entity, format, entityNode);
            redactedEntities.add(outcome.resource());
            if (!outcome.fields().isEmpty()) {
                List<RedactedField> nested = nestRedactions(outcome.fields(), "$.entities[" + index + "]");
                nested.forEach(field -> validateJsonPath(originalNode, field.prePath()));
                combinedRedactions.addAll(nested);
            }
        }

        boolean addedDynamic = combinedRedactions.size() > originalRedactionCount;
        List<String> conformance = addedDynamic
                ? addRedactedConformance(resource.rdapConformance())
                : resource.rdapConformance();
        List<RedactedField> redacted = combinedRedactions.isEmpty() ? null : List.copyOf(combinedRedactions);

        return new DomainResource(
                conformance,
                redacted,
                resource.objectClassName(),
                resource.handle(),
                resource.ldhName(),
                resource.unicodeName(),
                resource.status(),
                resource.events(),
                resource.notices(),
                List.copyOf(redactedEntities),
                resource.links(),
                resource.nameservers(),
                resource.secureDNS(),
                resource.versioningData()
        );
    }

    public DomainSearchResponse redact(DomainSearchResponse response, ContactFormat format) {
        if (response == null || response.domainSearchResults() == null) {
            return response;
        }
        List<DomainResource> results = response.domainSearchResults();
        List<DomainResource> redactedResults = new ArrayList<>(results.size());
        List<RedactedField> combined = response.redacted() == null
                ? new ArrayList<>()
                : new ArrayList<>(response.redacted());
        int originalCount = combined.size();

        for (int index = 0; index < results.size(); index++) {
            DomainResource domainResource = results.get(index);
            DomainResource redacted = redact(domainResource, format);
            redactedResults.add(redacted);
            if (redacted.redacted() != null && !redacted.redacted().isEmpty()) {
                List<RedactedField> nested = nestRedactions(redacted.redacted(), "$.domainSearchResults[" + index + "]");
                combined.addAll(nested);
            }
        }

        boolean addedDynamic = combined.size() > originalCount;
        List<String> conformance = addedDynamic
                ? addRedactedConformance(response.rdapConformance())
                : response.rdapConformance();
        List<RedactedField> redacted = combined.isEmpty() ? null : List.copyOf(combined);

        return new DomainSearchResponse(
                conformance,
                redacted,
                List.copyOf(redactedResults),
                response.notices(),
                response.links()
        );
    }

    public RdapEntity redact(RdapEntity entity, ContactFormat format) {
        if (entity == null) {
            return null;
        }
        JsonNode entityNode = objectMapper.valueToTree(entity);
        RedactionOutcome<RdapEntity> outcome = redactEntityInternal(entity, format, entityNode);
        return outcome.fields().isEmpty() ? entity : outcome.resource();
    }

    public EntitySearchResponse redact(EntitySearchResponse response, ContactFormat format) {
        if (response == null || response.entitySearchResults() == null) {
            return response;
        }
        List<RdapEntity> results = response.entitySearchResults();
        List<RdapEntity> redactedResults = new ArrayList<>(results.size());
        List<RedactedField> combined = response.redacted() == null
                ? new ArrayList<>()
                : new ArrayList<>(response.redacted());
        int originalCount = combined.size();

        for (int index = 0; index < results.size(); index++) {
            RdapEntity entity = results.get(index);
            RdapEntity redacted = redact(entity, format);
            redactedResults.add(redacted);
            if (redacted.redacted() != null && !redacted.redacted().isEmpty()) {
                List<RedactedField> nested = nestRedactions(redacted.redacted(), "$.entitySearchResults[" + index + "]");
                combined.addAll(nested);
            }
        }

        boolean addedDynamic = combined.size() > originalCount;
        List<String> conformance = addedDynamic
                ? addRedactedConformance(response.rdapConformance())
                : response.rdapConformance();
        List<RedactedField> redacted = combined.isEmpty() ? null : List.copyOf(combined);

        return new EntitySearchResponse(
                conformance,
                redacted,
                List.copyOf(redactedResults),
                response.notices(),
                response.links()
        );
    }

    private RedactionOutcome<RdapEntity> redactEntityInternal(RdapEntity entity,
                                                             ContactFormat format,
                                                             JsonNode entityNode) {
        if (entity == null || entity.handle() == null || !REGISTRANT_HANDLE.equalsIgnoreCase(entity.handle())) {
            return new RedactionOutcome<>(entity, List.of());
        }

        List<RedactedField> fields = new ArrayList<>();
        Object updatedVcard = entity.vcardArray();
        JsContactCard updatedCard = entity.jsContactCard();

        if (format == ContactFormat.JCARD && entity.vcardArray() != null) {
            VcardRedactionResult result = redactRegistrantVcard(entity.vcardArray());
            if (!result.fields().isEmpty()) {
                updatedVcard = result.vcard();
                fields.addAll(result.fields());
            }
        } else if (format == ContactFormat.JSCONTACT && entity.jsContactCard() != null) {
            JsContactRedactionResult result = redactRegistrantJsContact(entity.jsContactCard());
            if (!result.fields().isEmpty()) {
                updatedCard = result.card();
                fields.addAll(result.fields());
            }
        }

        if (fields.isEmpty()) {
            return new RedactionOutcome<>(entity, List.of());
        }

        fields.forEach(field -> validateJsonPath(entityNode, field.prePath()));

        List<String> conformance = addRedactedConformance(entity.rdapConformance());
        List<RedactedField> immutableFields = List.copyOf(fields);
        RdapEntity updated = new RdapEntity(
                conformance,
                immutableFields,
                entity.objectClassName(),
                entity.handle(),
                entity.roles(),
                updatedVcard,
                updatedCard,
                entity.publicIds(),
                entity.links(),
                entity.events(),
                entity.status(),
                entity.remarks(),
                entity.itNicDnssec()
        );
        return new RedactionOutcome<>(updated, immutableFields);
    }

    private VcardRedactionResult redactRegistrantVcard(Object vcardArray) {
        if (!(vcardArray instanceof List<?> card) || card.size() < 2) {
            return new VcardRedactionResult(vcardArray, List.of());
        }
        List<Object> cardCopy = asMutableList(deepCopy(card));
        if (cardCopy.size() < 2) {
            return new VcardRedactionResult(vcardArray, List.of());
        }

        List<Object> properties = asMutableList(cardCopy.get(1));
        List<RedactedField> fields = new ArrayList<>();

        for (int index = 0; index < properties.size(); index++) {
            Object propertyObj = properties.get(index);
            if (!(propertyObj instanceof List<?> propertyList) || propertyList.size() < 4) {
                continue;
            }
            List<Object> property = asMutableList(propertyList);
            String name = Objects.toString(property.get(0), "").toLowerCase(Locale.ROOT);

            if ("fn".equals(name)) {
                property.set(3, "");
                properties.set(index, property);
                fields.add(buildField(
                        "Registrant full name",
                        "$.vcardArray[1][" + index + "][3]",
                        "$.vcardArray[1][" + index + "][3]",
                        "emptyValue",
                        "Registrant full name withheld for privacy."
                ));
            } else if ("email".equals(name)) {
                String original = property.get(3) != null ? property.get(3).toString() : "";
                property.set(3, maskEmail(original));
                properties.set(index, property);
                fields.add(buildField(
                        "Registrant email",
                        "$.vcardArray[1][" + index + "][3]",
                        "$.vcardArray[1][" + index + "][3]",
                        "partial",
                        "Email local-part partially redacted for privacy."
                ));
            }
        }

        if (fields.isEmpty()) {
            return new VcardRedactionResult(vcardArray, List.of());
        }

        cardCopy.set(1, properties);
        return new VcardRedactionResult(List.copyOf(cardCopy), fields);
    }

    private JsContactRedactionResult redactRegistrantJsContact(JsContactCard card) {
        List<RedactedField> fields = new ArrayList<>();
        Map<String, JsContactEmail> emails = card.emails();
        Map<String, JsContactEmail> updatedEmails = emails;

        if (emails != null && !emails.isEmpty() && emails.containsKey("email1")) {
            Map<String, JsContactEmail> mutable = new LinkedHashMap<>(emails);
            mutable.remove("email1");
            updatedEmails = mutable.isEmpty() ? null : Map.copyOf(mutable);
            fields.add(buildField(
                    "Registrant JSContact email",
                    "$.jsContactCard.emails",
                    "$.jsContactCard.emails.email1",
                    "removal",
                    "Primary email removed for privacy."
            ));
        }

        JsContactName name = card.name();
        JsContactName updatedName = name;
        if (name != null && name.full() != null && !name.full().isBlank()) {
            updatedName = new JsContactName("REDACTED", name.components());
            fields.add(buildField(
                    "Registrant JSContact name",
                    "$.jsContactCard.name.full",
                    "$.jsContactCard.name.full",
                    "replacement",
                    "Full name replaced to comply with privacy policy."
            ));
        }

        if (fields.isEmpty()) {
            return new JsContactRedactionResult(card, List.of());
        }

        JsContactCard updatedCard = new JsContactCard(
                card.uid(),
                card.kind(),
                updatedName,
                updatedEmails,
                card.phones(),
                card.addresses(),
                card.organizations(),
                card.roles()
        );
        return new JsContactRedactionResult(updatedCard, fields);
    }

    private List<RedactedField> nestRedactions(List<RedactedField> fields, String prefix) {
        if (fields == null || fields.isEmpty()) {
            return List.of();
        }
        List<RedactedField> nested = new ArrayList<>(fields.size());
        for (RedactedField field : fields) {
            nested.add(new RedactedField(
                    field.name(),
                    nestPath(prefix, field.prePath()),
                    nestPath(prefix, field.path()),
                    field.pathLang(),
                    field.method(),
                    field.reason(),
                    field.description()
            ));
        }
        return nested;
    }

    private RedactedField buildField(String nameDescription,
                                     String prePath,
                                     String path,
                                     String method,
                                     String detail) {
        List<String> description = detail == null ? null : List.of(detail);
        return new RedactedField(
                new RedactedField.RedactionDescriptor(nameDescription, null),
                prePath,
                path,
                JSON_PATH_LANG,
                method,
                DEFAULT_REASON,
                description
        );
    }

    private String nestPath(String prefix, String path) {
        if (path == null || path.isBlank()) {
            return prefix;
        }
        if (!path.startsWith("$")) {
            throw new IllegalArgumentException("JSONPath must start with '$': " + path);
        }
        if ("$".equals(path)) {
            return prefix;
        }
        return prefix + path.substring(1);
    }

    private void validateJsonPath(JsonNode root, String jsonPath) {
        if (root == null || jsonPath == null) {
            throw new IllegalArgumentException("JSONPath validation requires non-null root and path");
        }
        try {
            Object result = JsonPath.using(jsonPathConfiguration).parse(root).read(jsonPath);
            if (result instanceof List<?> list && list.isEmpty()) {
                throw new IllegalStateException("JSONPath " + jsonPath + " did not match any element");
            }
            if (result == null) {
                throw new IllegalStateException("JSONPath " + jsonPath + " resolved to null");
            }
        } catch (PathNotFoundException ex) {
            throw new IllegalStateException("JSONPath " + jsonPath + " not found", ex);
        }
    }

    private List<String> addRedactedConformance(List<String> base) {
        if (base == null || base.isEmpty()) {
            return List.of(REDACTED_CONFORMANCE);
        }
        if (base.contains(REDACTED_CONFORMANCE)) {
            return base;
        }
        List<String> updated = new ArrayList<>(base);
        updated.add(REDACTED_CONFORMANCE);
        return List.copyOf(updated);
    }

    private Object deepCopy(Object value) {
        if (value instanceof List<?> list) {
            List<Object> copy = new ArrayList<>(list.size());
            for (Object element : list) {
                copy.add(deepCopy(element));
            }
            return copy;
        }
        if (value instanceof Map<?, ?> map) {
            Map<Object, Object> copy = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                copy.put(entry.getKey(), deepCopy(entry.getValue()));
            }
            return copy;
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private List<Object> asMutableList(Object value) {
        if (value instanceof List<?> list) {
            return (List<Object>) value;
        }
        return new ArrayList<>();
    }

    private String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return "";
        }
        int atIndex = email.indexOf('@');
        if (atIndex < 1 || atIndex >= email.length() - 1) {
            return "***@redacted.invalid";
        }
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1);
        String maskedLocal = localPart.length() <= 3
                ? localPart.charAt(0) + "***"
                : localPart.substring(0, 3) + "***";
        return maskedLocal + "@" + domain;
    }

    private record RedactionOutcome<T>(T resource, List<RedactedField> fields) {
    }

    private record VcardRedactionResult(Object vcard, List<RedactedField> fields) {
    }

    private record JsContactRedactionResult(JsContactCard card, List<RedactedField> fields) {
    }
}
