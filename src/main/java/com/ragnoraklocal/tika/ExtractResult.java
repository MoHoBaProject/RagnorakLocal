package com.vertasquad.tika;

import java.util.Map;

public record ExtractResult(String filename, String contentType, String text, Map<String, String> metadata) {
}
