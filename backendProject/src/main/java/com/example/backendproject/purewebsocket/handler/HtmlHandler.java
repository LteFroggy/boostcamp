package com.example.backendproject.purewebsocket.handler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlHandler {
    @GetMapping("/asdfasdfasdf")
    public String index() {
        return "redirect:/purechat1.html";
    }
}
