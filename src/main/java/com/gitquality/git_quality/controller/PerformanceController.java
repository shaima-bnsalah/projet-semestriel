package com.gitquality.git_quality.controller;

import com.gitquality.git_quality.model.MemberPerformance;
import com.gitquality.git_quality.service.PerformanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
@CrossOrigin(origins = "*")
public class PerformanceController {

    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @PostMapping("/sync-git")
public ResponseEntity<MemberPerformance> sync(@RequestBody MemberPerformance req) {
    MemberPerformance updated = performanceService.processGitData(
        req.getAuthor(),      // L'email ou nom envoyé par P1
        req.getCommitCount(),
        req.getLinesAdded(),
        req.getLinesDeleted(),
        req.getFilesModified(),
        req.getLastCommitDate()
    );
    return ResponseEntity.ok(updated);
}

    // 📤 Pour le Frontend
    @GetMapping("/leaderboard")
    public ResponseEntity<List<MemberPerformance>> getLeaderboard() {
        return ResponseEntity.ok(performanceService.getLeaderboard());
    }
}