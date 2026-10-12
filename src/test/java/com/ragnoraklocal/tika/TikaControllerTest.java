package com.vertasquad.tika;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TikaControllerTest {

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new TikaController(new TikaService())).build();
    }

    @Test
    void health() throws Exception {
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void detectText() throws Exception {
        MockMultipartFile f = new MockMultipartFile("file", "a.txt", "text/plain", "Hello Tika".getBytes());
        mvc.perform(multipart("/api/detect").file(f))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("text/plain")));
    }

    @Test
    void extractText() throws Exception {
        MockMultipartFile f = new MockMultipartFile("file", "a.txt", "text/plain", "Hello Tika".getBytes());
        mvc.perform(multipart("/api/extract").file(f))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(containsString("Hello Tika")));
    }

    @Test
    void extractHtml() throws Exception {
        String html = "<html><head><title>T1</title></head><body><p>Body text</p></body></html>";
        MockMultipartFile f = new MockMultipartFile("file", "p.html", "text/html", html.getBytes());
        mvc.perform(multipart("/api/extract").file(f))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(containsString("Body text")))
                .andExpect(jsonPath("$.contentType").value(containsString("html")));
    }

    @Test
    void metadataEndpoint() throws Exception {
        MockMultipartFile f = new MockMultipartFile("file", "a.txt", "text/plain", "Hello".getBytes());
        mvc.perform(multipart("/api/metadata").file(f))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Content-Type']").exists());
    }

    @Test
    void emptyFileReturns400() throws Exception {
        MockMultipartFile f = new MockMultipartFile("file", "e.txt", "text/plain", new byte[0]);
        mvc.perform(multipart("/api/extract").file(f))
                .andExpect(status().isBadRequest());
    }
}
