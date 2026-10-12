package com.vertasquad.tika;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TikaService {

    private final Tika tika = new Tika();
    private final AutoDetectParser parser = new AutoDetectParser();

    /** Detect MIME type only. */
    public String detect(MultipartFile file) throws IOException {
        try (InputStream in = file.getInputStream()) {
            return tika.detect(in, file.getOriginalFilename());
        }
    }

    /** Extract text + metadata. */
    public ExtractResult extract(MultipartFile file) throws IOException, TikaException, SAXException {
        BodyContentHandler handler = new BodyContentHandler(-1); // no size limit
        Metadata md = new Metadata();
        md.set(TikaCoreProperties.RESOURCE_NAME_KEY, file.getOriginalFilename());
        try (InputStream in = file.getInputStream()) {
            parser.parse(in, handler, md, new ParseContext());
        }
        return new ExtractResult(file.getOriginalFilename(), md.get(Metadata.CONTENT_TYPE),
                handler.toString().trim(), toMap(md));
    }

    /** Metadata only (text left out). */
    public Map<String, String> metadata(MultipartFile file) throws IOException, TikaException, SAXException {
        return extract(file).metadata();
    }

    private Map<String, String> toMap(Metadata md) {
        Map<String, String> map = new LinkedHashMap<>();
        for (String name : md.names()) {
            map.put(name, String.join(", ", md.getValues(name)));
        }
        return map;
    }
}
