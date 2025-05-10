package de.entwickler.training.spring.ai.rag.controller;

import de.entwickler.training.spring.ai.rag.model.QueryHistory;
import de.entwickler.training.spring.ai.rag.repository.QueryHistoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    private final QueryHistoryRepository queryHistoryRepository;

    @Autowired
    public ViewController(QueryHistoryRepository queryHistoryRepository) {
        this.queryHistoryRepository = queryHistoryRepository;
    }

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
        model.addAttribute("isHistoryPage", requestURI.startsWith("/history"));
        return "manage";
    }

    @GetMapping("/rag")
    public String ragPage(Model model, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        model.addAttribute("isManagePage", requestURI.startsWith("/manage"));
        model.addAttribute("isRagPage", requestURI.startsWith("/rag"));
        model.addAttribute("isHistoryPage", requestURI.startsWith("/history"));
        return "query";
    }

    @GetMapping("/history")
    public String historyPage(Model model, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        model.addAttribute("isManagePage", requestURI.startsWith("/manage"));
        model.addAttribute("isRagPage", requestURI.startsWith("/rag"));
        model.addAttribute("isHistoryPage", requestURI.startsWith("/history"));

        // Get the query history
        model.addAttribute("queryHistory", queryHistoryRepository.findMostRecentQueries(100));

        return "history";
    }
}
