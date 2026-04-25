package com.gitquality.git_quality.controller;

import com.gitquality.git_quality.model.MemberPerformance;
import com.gitquality.git_quality.service.PerformanceService;
import com.gitquality.git_quality.git_engine.GitAnalyseur; // 🟢 Import du moteur
import com.gitquality.git_quality.git_engine.StatUtilisateur; // 🟢 Import du modèle de P1
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/performance")
@CrossOrigin(origins = "*")
public class PerformanceController {

    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

   @PostMapping("/analyze-local")
public ResponseEntity<?> analyzeLocalRepo(@RequestParam String path) {
    try {
        // 🟢 ÉTAPE 1 : Vider les anciennes stats pour que le dashboard soit frais
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
                s.getLastCommitDate()
            );
        }
        return ResponseEntity.ok("Analyse réussie !");
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
    }
}
    @PostMapping("/sync-git")
    public ResponseEntity<MemberPerformance> sync(@RequestBody MemberPerformance req) {
        MemberPerformance updated = performanceService.processGitData(
            req.getAuthor(),
            req.getCommitCount(),
            req.getLinesAdded(),
            req.getLinesDeleted(),
            req.getFilesModified(),
            req.getLastCommitDate()
        );
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<MemberPerformance>> getLeaderboard() {
        return ResponseEntity.ok(performanceService.getLeaderboard());
    }
}