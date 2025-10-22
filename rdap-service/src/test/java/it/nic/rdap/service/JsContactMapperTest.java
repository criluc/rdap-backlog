package it.nic.rdap.service;

import it.nic.rdap.model.jscontact.JsContactCard;
import it.nic.rdap.model.jscontact.JsContactEmail;
import it.nic.rdap.model.jscontact.JsContactName;
import it.nic.rdap.model.jscontact.JsContactNameComponents;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JsContactMapperTest {

    private final JsContactMapper mapper = new JsContactMapper();

    @Test
    void fromRdapEntityMapsEmailAndName() {
        Object vcard = List.of("vcard", List.of(
                List.of("fn", Map.of(), "text", "Example Person"),
                List.of("n", Map.of(), "text", List.of("Person", "Example", "", "", "")),
                List.of("email", Map.of("type", List.of("work"), "pref", 1), "text", "user@example.it"),
                List.of("role", Map.of(), "text", "registrant")
        ));

        JsContactCard card = mapper.fromRdapEntity("HANDLE-1", vcard, List.of("registrant"));

        assertThat(card.uid()).isEqualTo("HANDLE-1");
        assertThat(card.name().full()).isEqualTo("Example Person");
        assertThat(card.emails()).hasSize(1);
        JsContactEmail primary = card.emails().values().iterator().next();
        assertThat(primary.value()).isEqualTo("user@example.it");
        assertThat(primary.contexts()).contains("work");
        assertThat(card.roles()).contains("registrant");
    }

    @Test
    void roundTripBetweenJsContactAndJcard() {
        JsContactCard card = new JsContactCard(
                "RID-1",
                "individual",
                new JsContactName("Example Person", new JsContactNameComponents("Example", "Person")),
                Map.of("primary", new JsContactEmail("person@example.it", List.of("work"), true)),
                Map.of(),
                Map.of(),
                Map.of(),
                List.of("registrant")
        );

        Object vcard = mapper.toVcard(card);
        JsContactCard reconstructed = mapper.fromRdapEntity("RID-1", vcard, List.of("registrant"));

        assertThat(reconstructed.name().full()).isEqualTo("Example Person");
        assertThat(reconstructed.roles()).containsExactly("registrant");
        assertThat(reconstructed.emails().values().iterator().next().value()).isEqualTo("person@example.it");
    }
}
