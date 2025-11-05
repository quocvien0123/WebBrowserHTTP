package com.example.simplehttpbrowser.model;

import java.util.Map;

public class ResponseData {
    private final int statusCode;
    private final String headers;
    private final String body;
    private Map<String, Integer> tagCount;

    public ResponseData(int statusCode, String headers, String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public Map<String, Integer> getTagCount() {
        return tagCount;
    }

    public void setTagCount(Map<String, Integer> tagCount) {
        this.tagCount = tagCount;
    }
}
