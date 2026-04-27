package com.gitquality.git_quality.controller;

import com.gitquality.git_quality.model.MemberPerformance;
import com.gitquality.git_quality.service.PerformanceService;
import com.gitquality.git_quality.git_engine.GitAnalyseur;
import com.gitquality.git_quality.git_engine.StatUtilisateur;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/performance")
@CrossOrigin(origins = "*")
public class PerformanceController {

    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @PostMapping("/analyze-local")
public ResponseEntity<?> analyze(@RequestParam String path) {
    try {
        performanceService.clearAllData();
        GitAnalyseur engine = new GitAnalyseur();
        Map<String, StatUtilisateur> results = engine.analyser(path);

        for (StatUtilisateur s : results.values()) {
            performanceService.processGitData(
                s.getAuthor(),
                s.getCommitCount(),
                s.getLinesAdded(),
                s.getLinesDeleted(),
                s.getFilesModified(),
                s.getDailyStats() 
            );
        }
        return ResponseEntity.ok("Analyse réussie !");
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
    }
}

    @GetMapping("/leaderboard")
    public ResponseEntity<List<MemberPerformance>> getLeaderboard() {
        return ResponseEntity.ok(performanceService.getLeaderboard());
    }
}