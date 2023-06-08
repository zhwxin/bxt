package xyz.zwxin.work.boxiaotong.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class PagesController {
    @GetMapping("/execl")
    public String execl() {
        return "splitexcel";
    }

    @GetMapping("/sku")
    public String skuSplit() {
        return "skusplit";
    }

}
