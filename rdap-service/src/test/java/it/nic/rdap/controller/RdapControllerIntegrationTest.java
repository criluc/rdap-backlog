package it.nic.rdap.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
                .andExpect(jsonPath("$.ldhName").value("example.it"))
                .andExpect(jsonPath("$.links[0].rel").value("self"));
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
    @DisplayName("Domain search filters by name fragment")
    void domainSearch() throws Exception {
        mockMvc.perform(get("/rdap/domains")
                        .queryParam("name", "example")
                        .accept("application/rdap+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.domainSearchResults[0].ldhName").value("example.it"));
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
    @DisplayName("Entity search requires fn or handle query parameter")
    void entitySearchMissingParam() throws Exception {
        mockMvc.perform(get("/rdap/entities")
                        .accept("application/rdap+json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(400));
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
}
