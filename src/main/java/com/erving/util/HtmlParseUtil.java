package com.erving.util;

import com.erving.dto.JdContent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author erving
 * @date 2020/6/15
 **/
@Component
public class HtmlParseUtil {

//    public static void main(String[] args) throws IOException {
//        new HtmlParseUtil().parseJD("java").forEach(System.out::println);
//    }

    public List<JdContent> parseJD(String keyword) throws IOException {
        String prefix = "https://search.jd.com/Search?keyword=";

        List<JdContent> list = new ArrayList<>();
        Document document = Jsoup.parse(new URL(prefix + keyword), 30000);
        Element goodsList = document.getElementById("J_goodsList");
        Elements goods = goodsList.getElementsByTag("li");
        for (Element good : goods) {
            String img = good.getElementsByTag("img").eq(0).attr("src");
            String price = good.getElementsByClass("p-price").eq(0).text();
            String title = good.getElementsByClass("p-name").eq(0).text();

            JdContent content = new JdContent(title, price, img);
            list.add(content);
        }
        return list;
    }

}
