package com.example.simplehttpbrowser.controller;

import com.example.simplehttpbrowser.parser.HTMLParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class BrowserController {

    @FXML private TextField urlField;
    @FXML private ComboBox<String> methodBox;
    @FXML private TextArea responseArea;
    @FXML private Label tagInfoLabel;
    @FXML private VBox postBox;
    @FXML private TextArea postBodyArea;
    @FXML private WebView webView;
    @FXML private TabPane tabPane;

    private final HTMLParser htmlParser = new HTMLParser();

    @FXML
    public void initialize() {
        methodBox.getItems().addAll("GET", "POST", "HEAD");
        methodBox.getSelectionModel().selectFirst();

        postBox.setVisible(false);
        postBox.setManaged(false);

        // Hiển thị/ẩn postBox khi chọn method
        methodBox.setOnAction(event -> {
            boolean isPost = methodBox.getValue().equals("POST");
            postBox.setVisible(isPost);
            postBox.setManaged(isPost);
        });

        // Bắt Enter trên thanh URL để load trực tiếp trang web
        urlField.setOnAction(event -> {
            String url = urlField.getText().trim();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }

            try {
                WebEngine engine = webView.getEngine();
                engine.load(url); // load trang web thực sự
                tabPane.getSelectionModel().select(0); // chuyển sang tab WebView (now index 0)
            } catch (Exception e) {
                showError("❌ Error loading URL: " + e.getMessage());
            }
        });
    }


    private String normalizeUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "http://" + url;
        }
        return url;
    }

    @FXML
    private void onSendRequest() {
        String method = methodBox.getValue();
        String url = normalizeUrl(urlField.getText().trim());

        if (url.isEmpty()) {
            showError("⚠ Please enter a valid URL.");
            return;
        }

        try {
            switch (method) {
                case "GET" -> handleGET(url);
                case "POST" -> handlePOST(url);
                case "HEAD" -> handleHEAD(url);
            }
        } catch (Exception e) {
            showError("❌ Error: " + e.getMessage());
        }
    }

    @FXML
    public void onClear(ActionEvent event) {
        urlField.clear();
        postBodyArea.clear();
        responseArea.clear();
        tagInfoLabel.setText("Ready");
        // Clear web view
        try {
            webView.getEngine().loadContent("");
        } catch (Exception ignored) {
        }
        tabPane.getSelectionModel().select(0);
    }

    private void showHtmlRendered(String html) {
        WebEngine engine = webView.getEngine();
        engine.loadContent(html, "text/html");
        tabPane.getSelectionModel().select(0); // Browser View is now index 0
    }

    private void showError(String msg) {
        responseArea.setText(msg);
        tagInfoLabel.setText("⚠ No data");
        tabPane.getSelectionModel().select(1); // show HTML Source (index 1)
    }

    private void analyzeHtml(String html) {
        Map<String, Integer> tagCount = htmlParser.countTags(html);

        tagInfoLabel.setText(
                "Length: " + html.length() +
                        " | <p>: " + tagCount.getOrDefault("p", 0) +
                        " | <div>: " + tagCount.getOrDefault("div", 0) +
                        " | <span>: " + tagCount.getOrDefault("span", 0) +
                        " | <img>: " + tagCount.getOrDefault("img", 0)
        );
    }

    private void handleGET(String url) throws IOException {
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Compatible; SimpleBrowser/1.0)")
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .get();

        String html = doc.outerHtml();
        responseArea.setText(html);
        analyzeHtml(html);
        showHtmlRendered(html);
    }

    private void handlePOST(String url) {
        try {
            var conn = Jsoup.connect(url)
                    .timeout(15000)
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .method(org.jsoup.Connection.Method.POST)
                    .header("User-Agent", "Mozilla/5.0")
                    .header("Content-Type", "application/x-www-form-urlencoded");

            String postData = postBodyArea.getText().trim();
            if (!postData.isEmpty()) {
                String[] pairs = postData.split("&");
                for (String pair : pairs) {
                    String[] kv = pair.split("=", 2);
                    if (kv.length == 2) conn.data(kv[0], kv[1]);
                }
            }

            var response = conn.execute();
            String contentType = response.contentType();

            responseArea.setText("Status: " + response.statusCode() +
                    "\nContent-Type: " + contentType + "\n\n");

            if (contentType != null && contentType.contains("html")) {
                Document doc = response.parse();
                String html = doc.outerHtml();
                responseArea.appendText(html);
                analyzeHtml(html);
                showHtmlRendered(html);
            } else {
                responseArea.appendText(response.body());
                tagInfoLabel.setText("⚠ No HTML to analyze.");
                tabPane.getSelectionModel().select(1); // switch to source
            }

        } catch (Exception e) {
            showError("❌ POST Error: " + e.getMessage() +
                    "\n⚠ Server có thể chặn POST hoặc không trả HTML!");
        }
    }

    private void handleHEAD(String urlStr) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("HEAD");

        StringBuilder sb = new StringBuilder("== HEAD Response ==\n");
        sb.append("Status: ").append(conn.getResponseCode())
                .append(" ").append(conn.getResponseMessage()).append("\n\n");

        conn.getHeaderFields().forEach((key, value) ->
                sb.append(key).append(": ").append(value).append("\n")
        );

        responseArea.setText(sb.toString());
        tagInfoLabel.setText("HEAD ✅ No HTML");
        tabPane.getSelectionModel().select(1); // show source
    }
}
