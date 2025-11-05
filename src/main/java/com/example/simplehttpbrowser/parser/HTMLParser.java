package com.example.simplehttpbrowser.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.HashMap;
import java.util.Map;

public class HTMLParser {

    public Map<String, Integer> countTags(String html) {
        Map<String, Integer> tagCount = new HashMap<>();
        if (html == null || html.isEmpty()) return tagCount;

        Document doc = Jsoup.parse(html);

        tagCount.put("p", doc.select("p").size());
        tagCount.put("div", doc.select("div").size());
        tagCount.put("span", doc.select("span").size());
        tagCount.put("img", doc.select("img").size());

        return tagCount;
    }
}
