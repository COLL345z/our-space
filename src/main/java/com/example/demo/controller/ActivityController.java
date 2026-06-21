package com.example.demo.controller;

import com.example.demo.entity.Activity;
import com.example.demo.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
public class ActivityController {

    @Autowired
    private ActivityRepository activityRepository;

    // GET all activities
    @GetMapping
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    // GET by id
    @GetMapping("/{id}")
    public Activity getActivityById(@PathVariable Long id) {
        return activityRepository.findById(id).orElse(null);
    }

    // CREATE activity
    @PostMapping
    public Activity createActivity(@RequestBody Activity activity) {
        // ensure ratings default to 0 if not provided
        if (activity.getRatingRehema() < 0 || activity.getRatingRehema() > 5) {
            activity.setRatingRehema(0);
        }
        if (activity.getRatingCollins() < 0 || activity.getRatingCollins() > 5) {
            activity.setRatingCollins(0);
        }
        return activityRepository.save(activity);
    }

    // UPDATE activity
    @PutMapping("/{id}")
    public Activity updateActivity(@PathVariable Long id, @RequestBody Activity updated) {
        return activityRepository.findById(id).map(activity -> {
            activity.setTitle(updated.getTitle());
            activity.setDescription(updated.getDescription());
            activity.setDate(updated.getDate());
            activity.setStatus(updated.getStatus());
            activity.setRatingRehema(updated.getRatingRehema());
            activity.setRatingCollins(updated.getRatingCollins());
            return activityRepository.save(activity);
        }).orElse(null);
    }

    // DELETE activity
    @DeleteMapping("/{id}")
    public void deleteActivity(@PathVariable Long id) {
        activityRepository.deleteById(id);
    }

    // GET by status
    @GetMapping("/status/{status}")
    public List<Activity> getByStatus(@PathVariable String status) {
        return activityRepository.findByStatus(status);
    }
}