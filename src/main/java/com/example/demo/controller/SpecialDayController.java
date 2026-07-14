package com.example.demo.controller;

import com.example.demo.entity.SpecialDay;
import com.example.demo.repository.SpecialDayRepository;
import com.example.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/special-days")
public class SpecialDayController {

    private final SpecialDayRepository repo;
    
    @Autowired
    private NotificationService notificationService;

    public SpecialDayController(SpecialDayRepository repo) {
        this.repo = repo;
    }

    // GET all special days
    @GetMapping
    public List<SpecialDay> getAll() {
        return repo.findAll();
    }

    // POST add special day
    @PostMapping
    public SpecialDay add(@RequestBody SpecialDay day) {
        SpecialDay saved = repo.save(day);
        
        // Send immediate notification when a special day is created
        notificationService.notifyPartner(
            day.getCreatedBy() != null ? day.getCreatedBy() : "Someone",
            "📅 New special day added!",
            day.getDescription() != null ? day.getDescription() : "A new special day has been added"
        );
        
        return saved;
    }

    // DELETE special day
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
