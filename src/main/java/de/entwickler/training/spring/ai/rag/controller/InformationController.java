package de.entwickler.training.spring.ai.rag.controller;

import de.entwickler.training.spring.ai.rag.model.Information;
import de.entwickler.training.spring.ai.rag.service.InformationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/information")
public class InformationController {

    private final InformationService informationService;

    @Autowired
    public InformationController(InformationService informationService) {
        this.informationService = informationService;
    }

    @GetMapping
    public List<Information> getAllInformation() {
        return informationService.getAllInformation();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Information> getInformationById(@PathVariable UUID id) {
        return informationService.getInformationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Information> createInformation(@Valid @RequestBody Information information) {
        Information savedInformation = informationService.saveInformation(information);
        return new ResponseEntity<>(savedInformation, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Information> updateInformation(@PathVariable UUID id, @Valid @RequestBody Information information) {
        return informationService.getInformationById(id)
                .map(existingInfo -> {
                    information.setId(id);
                    Information updatedInfo = informationService.saveInformation(information);
                    return ResponseEntity.ok(updatedInfo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInformation(@PathVariable UUID id) {
        return informationService.getInformationById(id)
                .map(info -> {
                    informationService.deleteInformation(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<Information> searchInformation(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String similar,
            @RequestParam(defaultValue = "10") int limit) {

        if (similar != null && !similar.isEmpty()) {
            return informationService.findSimilarInformation(similar, limit);
        } else if (title != null && !title.isEmpty()) {
            return informationService.searchByTitle(title);
        } else if (content != null && !content.isEmpty()) {
            return informationService.searchByContent(content);
        } else {
            return informationService.getAllInformation();
        }
    }
}
