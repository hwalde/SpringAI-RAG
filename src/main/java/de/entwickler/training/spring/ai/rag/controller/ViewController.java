package de.entwickler.training.spring.ai.rag.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String redirectToManage() {
        // Redirect requests from the root path to the /manage page
        return "redirect:/manage";
    }

    @GetMapping("/manage")
    public String managePage(Model model, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        // Flags zum Model hinzufügen
        model.addAttribute("isManagePage", requestURI.startsWith("/manage"));
        model.addAttribute("isRagPage", requestURI.startsWith("/rag"));
        return "manage";
    }

    @GetMapping("/rag")
    public String ragPage(Model model, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        model.addAttribute("isManagePage", requestURI.startsWith("/manage"));
        model.addAttribute("isRagPage", requestURI.startsWith("/rag"));
        return "query";
    }
}