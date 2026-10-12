package com.vertasquad.tika;

import org.apache.tika.exception.TikaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TikaController {

    private final TikaService service;

    public TikaController(TikaService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @PostMapping("/detect")
    public Map<String, String> detect(@RequestParam("file") MultipartFile file) throws IOException {
        requireFile(file);
        return Map.of("filename", String.valueOf(file.getOriginalFilename()),
                "contentType", service.detect(file));
    }

    @PostMapping("/extract")
    public ExtractResult extract(@RequestParam("file") MultipartFile file)
            throws IOException, TikaException, SAXException {
        requireFile(file);
        return service.extract(file);
    }

    @PostMapping("/metadata")
    public Map<String, String> metadata(@RequestParam("file") MultipartFile file)
            throws IOException, TikaException, SAXException {
        requireFile(file);
        return service.metadata(file);
    }

    private void requireFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is empty");
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler({IOException.class, TikaException.class, SAXException.class})
    public ResponseEntity<Map<String, String>> parseError(Exception e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", e.getMessage()));
    }
}
