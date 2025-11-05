package com.example.simplehttpbrowser;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MiniHttpServer {
    public static void main(String[] args) throws Exception {
        String serverIP = "0.0.0.0";
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverIP, port), 0);
        System.out.println("✅ Server started at http://localhost:8080/travel");

        // Context cho trang chính TravelPro
        server.createContext("/travel", new TravelHandler());

        // Context cho feedback
        server.createContext("/feedback", new FeedbackHandler());

        server.setExecutor(null);
        server.start();
    }

    // Handler trang chính
    static class TravelHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            String html = """
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>TravelPro — Explore</title>
  <style>
    :root{--bg:#f6f9fc;--card:#ffffff;--accent:#1f6feb;--muted:#6b7280}
    *{box-sizing:border-box}
    body{margin:0;font-family:Inter, system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial;background:var(--bg);color:#0f172a}
    header{background:linear-gradient(90deg,#0ea5e9 0%, #1f6feb 100%);color:white;padding:48px 20px;text-align:center}
    header h1{margin:0;font-size:36px;letter-spacing:-0.5px}
    header p{subtle;margin-top:8px;color:rgba(255,255,255,0.92);font-size:16px}
    .wrap{max-width:1100px;margin:24px auto;padding:0 18px}
    .search-card{background:var(--card);padding:18px;border-radius:12px;box-shadow:0 6px 18px rgba(16,24,40,0.06);display:flex;gap:12px;align-items:center}
    .search-card input{flex:1;padding:12px 14px;border:1px solid #e6eef8;border-radius:8px;font-size:15px}
    .search-card select{padding:10px;border-radius:8px;border:1px solid #e6eef8}
    .search-card button{background:var(--accent);color:white;border:none;padding:10px 14px;border-radius:8px;cursor:pointer}

    .grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(260px,1fr));gap:20px;margin-top:28px}
    .card{background:var(--card);border-radius:12px;overflow:hidden;box-shadow:0 8px 24px rgba(15,23,42,0.06);transition:transform .28s}
    .card:hover{transform:translateY(-6px)}
    .card img{width:100%;height:180px;object-fit:cover;display:block}
    .card .body{padding:16px}
    .card h3{margin:0 0 8px;font-size:18px}
    .card p{margin:0;color:var(--muted);font-size:14px}
    .card .actions{display:flex;justify-content:center;padding:14px}
    .btn-ghost{background:transparent;border:1px solid #e6eef8;padding:8px 12px;border-radius:8px;color:#0f172a;cursor:pointer}
    .btn-primary{background:var(--accent);color:white;border:none;padding:10px 16px;border-radius:8px;cursor:pointer}

    footer{margin-top:40px;padding:24px 18px;text-align:center;color:var(--muted);font-size:13px}

    @media (max-width:600px){header{padding:28px 12px} header h1{font-size:26px}}
  </style>
</head>
<body>
  <header>
    <h1>TravelPro — Your next adventure awaits</h1>
    <p>Hand-picked destinations, immersive experiences, and curated travel tips.</p>
  </header>

  <main class="wrap">
    <div class="search-card">
      <input id="q" placeholder="Search destinations, e.g. Bali, Tokyo, Paris"/>
      <select id="m">
        <option>GET</option>
        <option>POST</option>
        <option>HEAD</option>
      </select>
      <button onclick="doOpen()">Open</button>
    </div>

    <section class="grid" aria-label="featured">
      <article class="card">
        <img src="https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=1200&auto=format&fit=crop" alt="Beach">
        <div class="body">
          <h3>Sunny Beach</h3>
          <p>Golden sands and turquoise water — perfect for relaxation and water sports.</p>
        </div>
        <div class="actions"><button class="btn-primary" onclick="openCard('https://example.com')">Explore</button></div>
      </article>

      <article class="card">
        <img src="https://images.unsplash.com/photo-1501785888041-af3ef285b470?q=80&w=1200&auto=format&fit=crop" alt="Mountain">
        <div class="body">
          <h3>Mountain Adventure</h3>
          <p>Hike scenic trails and discover panoramic viewpoints.</p>
        </div>
        <div class="actions"><button class="btn-ghost" onclick="openCard('/feedback')">Feedback</button></div>
      </article>

      <article class="card">
        <img src="https://images.unsplash.com/photo-1519125323398-675f0ddb6308?q=80&w=1200&auto=format&fit=crop" alt="City">
        <div class="body">
          <h3>City Lights</h3>
          <p>Urban escapes rich in culture, food scenes, and nightlife.</p>
        </div>
        <div class="actions"><button class="btn-ghost" onclick="openCard('https://example.org')">Discover</button></div>
      </article>
    </section>

    <footer>
      <small>Built with ❤️ — Mini demo server • Try the <a href="/feedback">feedback form</a>.</small>
    </footer>
  </main>

  <script>
    function doOpen(){
      const q=document.getElementById('q').value.trim();
      const m=document.getElementById('m').value;
      let url = q || '/travel';
      if (!url.startsWith('http')) url = (m==='GET' || m==='HEAD') ? 'http://'+url : 'http://'+url;
      // just open in new tab to demonstrate
      window.open(url, '_blank');
    }
    function openCard(u){ window.open(u, '_blank'); }
  </script>
</body>
</html>
""";

            sendResponse(exchange, 200, html);
        }
    }

    // Handler feedback
    static class FeedbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            if (method.equalsIgnoreCase("HEAD")) {
                sendHeadersOnly(exchange, 200);
                return;
            }

            if (method.equalsIgnoreCase("GET")) {
                String html = """
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>Feedback — TravelPro</title>
  <style>
    body{font-family:Inter, system-ui, -apple-system, 'Segoe UI', Roboto, Arial;background:#f6f8fb;margin:0}
    .wrap{max-width:640px;margin:48px auto;padding:20px}
    .card{background:white;padding:22px;border-radius:12px;box-shadow:0 10px 30px rgba(2,6,23,0.06)}
    h2{margin:0 0 10px}
    label{display:block;margin-top:12px;font-size:14px;color:#334155}
    input[type=text], textarea{width:100%;padding:10px;border:1px solid #e6eef8;border-radius:8px;margin-top:6px}
    button{margin-top:14px;background:#0ea5e9;color:white;padding:10px 14px;border:none;border-radius:8px;cursor:pointer}
    .note{font-size:13px;color:#64748b;margin-top:10px}
  </style>
</head>
<body>
  <main class="wrap">
    <div class="card">
      <h2>Feedback</h2>
      <p class="note">We'd love to hear from you — this demo sends the form back and shows a confirmation.</p>
      <form method="POST" action="/feedback">
        <label for="name">Name</label>
        <input id="name" name="Name" type="text" placeholder="Your name" />

        <label for="answer">Your message</label>
        <textarea id="answer" name="answer" rows="4" placeholder="Tell us what you think"></textarea>

        <button type="submit">Send Feedback</button>
      </form>
    </div>
  </main>
</body>
</html>
""";
                sendResponse(exchange, 200, html);
                return;
            }

            if (!method.equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> form = parseForm(body);

            String name = form.getOrDefault("Name", "unknown");
            String answer = form.getOrDefault("answer", "(no message)");

            String html = """
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>Thank you — TravelPro</title>
  <style>
    body{font-family:Inter, system-ui, -apple-system, 'Segoe UI', Roboto, Arial;background:#f6f8fb;margin:0}
    .wrap{max-width:680px;margin:60px auto;padding:20px}
    .card{background:white;padding:26px;border-radius:12px;box-shadow:0 12px 30px rgba(2,6,23,0.06);text-align:center}
    h2{margin:0 0 8px}
    p{color:#475569}
    a{display:inline-block;margin-top:14px;padding:8px 14px;border-radius:8px;background:#0ea5e9;color:white;text-decoration:none}
    pre{background:#0f1720;color:#e6eef6;padding:12px;border-radius:8px;overflow:auto;text-align:left}
  </style>
</head>
<body>
  <main class="wrap">
    <div class="card">
      <h2>Thank you!</h2>
      <p>We received your feedback.</p>
      <p><strong>Name:</strong> %s</p>
      <p><strong>Message:</strong></p>
      <pre>%s</pre>

      <a href="/feedback">Send another</a>
    </div>
  </main>
</body>
</html>
""".formatted(escapeHtml(name), escapeHtml(answer));

            sendResponse(exchange, 200, html);
        }
    }

    private static Map<String, String> parseForm(String body) {
        Map<String, String> map = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2)
                map.put(decode(kv[0]), decode(kv[1]));
        }
        return map;
    }

    private static String decode(String s) {
        return java.net.URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#x27;");
    }

    private static void sendHeadersOnly(HttpExchange exchange, int status) throws IOException {
        exchange.getResponseHeaders().add("Server", "LocalJavaServer");
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        exchange.getResponseHeaders().add("Connection", "Keep-Alive");

        exchange.sendResponseHeaders(status, -1);
        exchange.close();
    }

    private static void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Server", "LocalJavaServer");
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        exchange.getResponseHeaders().add("Connection", "Keep-Alive");
        exchange.getResponseHeaders().add("Content-Length", String.valueOf(bytes.length));

        exchange.sendResponseHeaders(status, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
