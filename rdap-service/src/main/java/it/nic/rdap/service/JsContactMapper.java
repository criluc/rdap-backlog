package it.nic.rdap.service;

import it.nic.rdap.model.jscontact.JsContactAddress;
import it.nic.rdap.model.jscontact.JsContactCard;
import it.nic.rdap.model.jscontact.JsContactEmail;
import it.nic.rdap.model.jscontact.JsContactName;
import it.nic.rdap.model.jscontact.JsContactNameComponents;
import it.nic.rdap.model.jscontact.JsContactOrganisation;
import it.nic.rdap.model.jscontact.JsContactPhone;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class JsContactMapper {

    public JsContactCard fromRdapEntity(String handle, Object vcardArray, List<String> roles) {
        if (!(vcardArray instanceof List<?> card) || card.size() < 2) {
            return new JsContactCard(handle, "individual", null, Map.of(), Map.of(), Map.of(), Map.of(), roles);
        }

        List<?> properties = (List<?>) card.get(1);

        String fullName = null;
        JsContactNameComponents components = null;
        Map<String, JsContactEmail> emails = new LinkedHashMap<>();
        Map<String, JsContactPhone> phones = new LinkedHashMap<>();
        Map<String, JsContactAddress> addresses = new LinkedHashMap<>();
        Map<String, JsContactOrganisation> organizations = new LinkedHashMap<>();

        int emailIndex = 1;
        int phoneIndex = 1;
        int addressIndex = 1;

        for (Object propertyObj : properties) {
            if (!(propertyObj instanceof List<?> property) || property.size() < 4) {
                continue;
            }
            String propertyName = String.valueOf(property.get(0)).toLowerCase(Locale.ROOT);
            Object params = property.get(1);
            Object value = property.get(3);

            switch (propertyName) {
                case "fn" -> fullName = value != null ? value.toString() : null;
                case "n" -> components = parseNameComponents(value);
                case "email" -> {
                    String key = "email" + emailIndex++;
                    emails.put(key, new JsContactEmail(
                            value != null ? value.toString() : null,
                            parseContexts(params),
                            Boolean.TRUE.equals(parsePreferred(params))
                    ));
                }
                case "tel" -> {
                    String key = "phone" + phoneIndex++;
                    phones.put(key, new JsContactPhone(
                            value != null ? value.toString() : null,
                            parseContexts(params),
                            Boolean.TRUE.equals(parsePreferred(params))
                    ));
                }
                case "adr" -> {
                    String key = "address" + addressIndex++;
                    addresses.put(key, new JsContactAddress(
                            composeAddress(value),
                            parseCountry(params)
                    ));
                }
                case "org" -> organizations.put("org", new JsContactOrganisation(value != null ? value.toString() : null));
                default -> {
                    // ignore others for now
                }
            }
        }

        JsContactName name = null;
        if (fullName != null || components != null) {
            name = new JsContactName(fullName, components);
        }

        return new JsContactCard(
                handle,
                "individual",
                name,
                emails.isEmpty() ? Map.of() : Map.copyOf(emails),
                phones.isEmpty() ? Map.of() : Map.copyOf(phones),
                addresses.isEmpty() ? Map.of() : Map.copyOf(addresses),
                organizations.isEmpty() ? Map.of() : Map.copyOf(organizations),
                roles
        );
    }

    @SuppressWarnings("unchecked")
    public Object toVcard(JsContactCard card) {
        List<Object> properties = new ArrayList<>();

        if (card.name() != null && card.name().full() != null) {
            properties.add(List.of("fn", Map.of(), "text", card.name().full()));
        }
        if (card.name() != null && card.name().components() != null) {
            JsContactNameComponents cmp = card.name().components();
            List<Object> nValue = List.of(
                    cmp.surname() != null ? cmp.surname() : "",
                    cmp.given() != null ? cmp.given() : "",
                    "",
                    "",
                    ""
            );
            properties.add(List.of("n", Map.of(), "text", nValue));
        }

        if (card.emails() != null) {
            card.emails().forEach((key, email) -> {
                Map<String, Object> params = new LinkedHashMap<>();
                if (email.contexts() != null && !email.contexts().isEmpty()) {
                    params.put("type", email.contexts());
                }
                if (Boolean.TRUE.equals(email.preferred())) {
                    params.put("pref", 1);
                }
                properties.add(List.of("email", params, "text", email.value()));
            });
        }

        if (card.phones() != null) {
            card.phones().forEach((key, phone) -> {
                Map<String, Object> params = new LinkedHashMap<>();
                if (phone.contexts() != null && !phone.contexts().isEmpty()) {
                    params.put("type", phone.contexts());
                }
                if (Boolean.TRUE.equals(phone.preferred())) {
                    params.put("pref", 1);
                }
                properties.add(List.of("tel", params, "text", phone.value()));
            });
        }

        if (card.addresses() != null) {
            card.addresses().forEach((key, address) -> {
                Map<String, Object> params = new LinkedHashMap<>();
                if (address.country() != null) {
                    params.put("country", address.country());
                }
                properties.add(List.of("adr", params, "text", address.full()));
            });
        }

        if (card.organizations() != null) {
            card.organizations().forEach((key, org) -> properties.add(List.of("org", Map.of(), "text", org.name())));
        }

        if (card.roles() != null) {
            card.roles().forEach(role ->
                    properties.add(List.of("role", Map.of(), "text", role))
            );
        }

        return List.of("vcard", properties);
    }

    private JsContactNameComponents parseNameComponents(Object value) {
        if (!(value instanceof List<?> nValues) || nValues.size() < 2) {
            return null;
        }
        String surname = Optional.ofNullable(nValues.get(0)).map(Object::toString).filter(s -> !s.isBlank()).orElse(null);
        String given = Optional.ofNullable(nValues.get(1)).map(Object::toString).filter(s -> !s.isBlank()).orElse(null);
        if (surname == null && given == null) {
            return null;
        }
        return new JsContactNameComponents(given, surname);
    }

    private List<String> parseContexts(Object params) {
        Object type = extractParam(params, "type");
        if (type instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        if (type != null) {
            return List.of(type.toString());
        }
        return List.of();
    }

    private Boolean parsePreferred(Object params) {
        Object pref = extractParam(params, "pref");
        if (pref instanceof Number number) {
            return number.intValue() == 1;
        }
        if (pref instanceof String s) {
            return "1".equals(s);
        }
        return null;
    }

    private String parseCountry(Object params) {
        Object country = extractParam(params, "country");
        return country != null ? country.toString() : null;
    }

    private Object extractParam(Object params, String key) {
        if (params instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (Objects.equals(String.valueOf(entry.getKey()).toLowerCase(Locale.ROOT), key.toLowerCase(Locale.ROOT))) {
                    return entry.getValue();
                }
            }
        }
        if (params instanceof List<?> list) {
            for (int i = 0; i < list.size() - 1; i += 2) {
                if (String.valueOf(list.get(i)).equalsIgnoreCase(key)) {
                    return list.get(i + 1);
                }
            }
        }
        return null;
    }

    private String composeAddress(Object value) {
        if (value instanceof List<?> list) {
            return list.stream()
                    .map(obj -> obj != null ? obj.toString() : "")
                    .filter(s -> !s.isBlank())
                    .reduce((left, right) -> left + ", " + right)
                    .orElse("");
        }
        return value != null ? value.toString() : null;
    }
}
