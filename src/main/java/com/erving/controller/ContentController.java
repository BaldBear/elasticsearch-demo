package com.erving.controller;

import com.erving.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author erving
 * @date 2020/6/15
 **/
@RestController
public class ContentController {

    @Autowired
    ContentService contentService;

    @GetMapping("/parse/{keyword}")
    public boolean parseJd(@PathVariable("keyword") String keyword){
        return contentService.parseContent(keyword);
    }

    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> search(@PathVariable("keyword") String keyword,
                                            @PathVariable("pageNo") int pageNo,
                                            @PathVariable("pageSize") int pageSize){
        return contentService.searchPage(keyword, pageNo, pageSize);
    }
}
