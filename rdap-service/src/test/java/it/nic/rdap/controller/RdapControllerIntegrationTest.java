package it.nic.rdap.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RdapControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Domain lookup returns RFC 9083 compliant structure")
    void domainLookup() throws Exception {
        mockMvc.perform(get("/rdap/domain/example.it")
                        .accept("application/rdap+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/rdap+json"))
                .andExpect(jsonPath("$.objectClassName").value("domain"))
                .andExpect(jsonPath("$.rdapConformance", containsInAnyOrder("rdap_level_0", "redacted")))
                .andExpect(jsonPath("$.ldhName").value("example.it"))
                .andExpect(jsonPath("$.links[0].rel").value("self"))
                .andExpect(jsonPath("$.redacted[0].method").value("emptyValue"))
                .andExpect(jsonPath("$.redacted[0].prePath").value("$.entities[2].vcardArray[1][0][3]"))
                .andExpect(jsonPath("$.redacted[1].method").value("partial"))
                .andExpect(jsonPath("$.entities[2].vcardArray[1][0][3]").value(""))
                .andExpect(jsonPath("$.entities[2].vcardArray[1][2][3]").value("reg***@example.it"));
    }

    @Test
    @DisplayName("Domain lookup honours ETag and Cache-Control")
    void domainLookupConditional() throws Exception {
        MvcResult result = mockMvc.perform(get("/rdap/domain/example.it")
                        .accept("application/rdap+json"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(header().string("ETag", notNullValue()))
                .andReturn();

        String etag = result.getResponse().getHeader("ETag");

        mockMvc.perform(get("/rdap/domain/example.it")
                        .header("If-None-Match", etag)
                        .accept("application/rdap+json"))
                .andExpect(status().isNotModified())
                .andExpect(header().string("ETag", etag));
    }

    @Test
    @DisplayName("JSContact negotiation exposes jsContactCard and conformance extension")
    void domainLookupJsContact() throws Exception {
        mockMvc.perform(get("/rdap/domain/example.it")
                        .accept("application/rdap+json;ext=jscontact"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rdapConformance", containsInAnyOrder("rdap_level_0", "draft-ietf-regext-rdap-jscontact", "redacted")))
                .andExpect(jsonPath("$.entities[0].jsContactCard.uid").value("NIC-REG"))
                .andExpect(jsonPath("$.entities[0].vcardArray").doesNotExist())
                .andExpect(jsonPath("$.entities[2].jsContactCard.name.full").value("REDACTED"))
                .andExpect(jsonPath("$.entities[2].jsContactCard.emails").doesNotExist())
                .andExpect(jsonPath("$.redacted[*].method", containsInAnyOrder("removal", "replacement")));
    }

    @Test
    @DisplayName("Unknown domain produces RDAP error payload")
    void domainNotFound() throws Exception {
        mockMvc.perform(get("/rdap/domain/unknown.it")
                        .accept("application/rdap+json"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/rdap+json"))
                .andExpect(jsonPath("$.errorCode").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    @DisplayName("Unsupported Accept header responds with 406 and RDAP error body")
    void acceptHeaderNotSupported() throws Exception {
        mockMvc.perform(get("/rdap/domain/example.it")
                        .accept("text/html"))
                .andExpect(status().isNotAcceptable())
                .andExpect(header().string("Content-Type", "application/rdap+json"))
                .andExpect(jsonPath("$.errorCode").value(406));
    }

    @Test
    @DisplayName("Domain search filters by name fragment")
    void domainSearch() throws Exception {
        mockMvc.perform(get("/rdap/domains")
                        .queryParam("name", "example")
                        .accept("application/rdap+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.domainSearchResults[0].ldhName").value("example.it"))
                .andExpect(jsonPath("$.domainSearchResults[0].rdapConformance", containsInAnyOrder("rdap_level_0", "redacted")))
                .andExpect(jsonPath("$.redacted[0].prePath").value("$.domainSearchResults[0].entities[2].vcardArray[1][0][3]"));
    }

    @Test
    @DisplayName("Domain search inherits RDAP media type")
    void domainSearchContentType() throws Exception {
        mockMvc.perform(get("/rdap/domains")
                        .queryParam("name", "nic")
                        .accept("application/rdap+json"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/rdap+json"));
    }

    @Test
    @DisplayName("Nameserver lookup exposes expected fields")
    void nameserverLookup() throws Exception {
        mockMvc.perform(get("/rdap/nameserver/ns1.nic.it")
                        .accept("application/rdap+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.objectClassName").value("nameserver"))
                .andExpect(jsonPath("$.ipAddresses[0]").value("192.0.2.53"));
    }

    @Test
    @DisplayName("Registrant entity response includes redaction metadata")
    void registrantEntityRedaction() throws Exception {
        mockMvc.perform(get("/rdap/entity/SH8013-REGISTRANT")
                        .accept("application/rdap+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rdapConformance", containsInAnyOrder("rdap_level_0", "redacted")))
                .andExpect(jsonPath("$.redacted[0].method").value("emptyValue"))
                .andExpect(jsonPath("$.vcardArray[1][0][3]").value(""))
                .andExpect(jsonPath("$.vcardArray[1][2][3]").value("reg***@example.it"));
    }

    @Test
    @DisplayName("Entity search requires fn or handle query parameter")
    void entitySearchMissingParam() throws Exception {
        mockMvc.perform(get("/rdap/entities")
                        .accept("application/rdap+json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(400));
    }

    @Test
    @DisplayName("Entity search aggregates redacted metadata from results")
    void entitySearchRedaction() throws Exception {
        mockMvc.perform(get("/rdap/entities")
                        .queryParam("handle", "SH8013")
                        .accept("application/rdap+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entitySearchResults[0].handle").value("SH8013-REGISTRANT"))
                .andExpect(jsonPath("$.entitySearchResults[0].rdapConformance", containsInAnyOrder("rdap_level_0", "redacted")))
                .andExpect(jsonPath("$.redacted[0].prePath").value("$.entitySearchResults[0].vcardArray[1][0][3]"));
    }

    @Test
    @DisplayName("IP lookup supports CIDR notation")
    void ipLookup() throws Exception {
        mockMvc.perform(get("/rdap/ip/192.0.2.0/24")
                        .accept("application/rdap+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.objectClassName").value("ip network"))
                .andExpect(jsonPath("$.ipVersion").value("v4"));
    }

    @Test
    @DisplayName("Autnum lookup returns matching handle")
    void autnumLookup() throws Exception {
        mockMvc.perform(get("/rdap/autnum/196672")
                        .accept("application/rdap+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.objectClassName").value("autnum"))
                .andExpect(jsonPath("$.handle").value("AS196672"));
    }

    @Test
    @DisplayName("Help endpoint returns informational text")
    void helpEndpoint() throws Exception {
        mockMvc.perform(get("/rdap/help")
                        .accept("application/rdap+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Italian RDAP Service"))
                .andExpect(jsonPath("$.links[0].rel").value("service"));
    }

    @Test
    @DisplayName("Help endpoint supports cache validators")
    void helpEndpointCaching() throws Exception {
        MvcResult result = mockMvc.perform(get("/rdap/help")
                        .accept("application/rdap+json"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "public, max-age=86400"))
                .andExpect(header().string("Last-Modified", notNullValue()))
                .andExpect(header().string("ETag", notNullValue()))
                .andReturn();

        String etag = result.getResponse().getHeader("ETag");

        mockMvc.perform(get("/rdap/help")
                        .header("If-None-Match", etag)
                        .accept("application/rdap+json"))
                .andExpect(status().isNotModified())
                .andExpect(header().string("ETag", etag));
    }

    @Test
    @DisplayName("Rate limit guard maps to 429 with Retry-After")
    void rateLimitExceeded() throws Exception {
        mockMvc.perform(get("/rdap/domain/example.it")
                        .header("X-Test-RateLimit", "exceeded")
                        .accept("application/rdap+json"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "30"))
                .andExpect(jsonPath("$.errorCode").value(429))
                .andExpect(jsonPath("$.title").value("Too Many Requests"));
    }
}
