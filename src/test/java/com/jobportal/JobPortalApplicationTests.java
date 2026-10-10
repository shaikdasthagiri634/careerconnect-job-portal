package com.jobportal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Runs against the in-memory H2 database with the demo data loaded. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class JobPortalApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void homePageLoads() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    void publicJobSearchReturnsSeededJobs() throws Exception {
        mvc.perform(get("/api/jobs").param("keyword", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    void postingJobRequiresEmployerRole() throws Exception {
        String body = """
                {"title":"Test","companyName":"Acme","location":"Remote","jobType":"REMOTE","description":"Test job"}
                """;
        mvc.perform(post("/api/jobs").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/jobs").with(httpBasic("candidate@gmail.com", "candidate123"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/jobs").with(httpBasic("employer@techcorp.com", "employer123"))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test"));
    }

    @Test
    void invalidJobIsRejectedWithFieldErrors() throws Exception {
        mvc.perform(post("/api/jobs").with(httpBasic("employer@techcorp.com", "employer123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    @Test
    void employerPagesNeedLogin() throws Exception {
        mvc.perform(get("/employer/dashboard")).andExpect(status().is3xxRedirection());
    }
}
