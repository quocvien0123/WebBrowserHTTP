package com.example.simplehttpbrowser.network;

import com.example.simplehttpbrowser.model.ResponseData;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPClient {

    public ResponseData sendRequest(String urlStr, String method) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);

        // Cho phép gửi body nếu POST
        if (method.equals("POST")) {
            conn.setDoOutput(true);
            String body = "example=data&student=vku";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
            }
        }

        int status = conn.getResponseCode();

        // Đọc header
        StringBuilder headerBuilder = new StringBuilder();
        conn.getHeaderFields().forEach((key, value) -> {
            if (key != null) headerBuilder.append(key).append(": ").append(String.join(",", value)).append("\n");
        });

        // Đọc body nếu không phải HEAD
        StringBuilder bodyBuilder = new StringBuilder();
        if (!method.equals("HEAD")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                bodyBuilder.append(line).append("\n");
            }
            reader.close();
        }

        conn.disconnect();
        return new ResponseData(status, headerBuilder.toString(), bodyBuilder.toString());
    }
}
