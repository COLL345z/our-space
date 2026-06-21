package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
    

@GetMapping("/our-space")
public String space(HttpSession session) {

    Boolean loggedIn =
            (Boolean) session.getAttribute("loggedIn");

    if (loggedIn == null || !loggedIn) {
        return "redirect:/";
    }

    return "space";
}
    @GetMapping("/special-days")
    public String specialDays() {
        return "special-days";
    }
    @GetMapping("/gallery")
    public String gallery() {
        return "gallery";
    }

    @GetMapping("/song")
    public String song() {
        return "song";
    }
    @GetMapping("/activities")
    public String activities() {
        return "activities";
    }
     @GetMapping("/login")
    public String login() {
        return "login"; // or whatever page you want
    }
    @GetMapping("/letters")
    public String letters() {
        return "letters";
    }
     
    @GetMapping("/memories")
public String memories() {
    return "memories";
}


}

  