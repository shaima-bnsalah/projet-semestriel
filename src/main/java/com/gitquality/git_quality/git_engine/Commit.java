package com.gitquality.git_quality.git_engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Commit {
    private String hash;
    private int userId;      // 🟢 Vérifie que cette ligne est bien là
    private String author;
    private String date;
    private int linesAdded;
    private int linesDeleted;
    private int filesModified;
    private String message;

    // Si Lombok ne fonctionne pas sur ton PC, ajoute ceci manuellement :
    public int getUserId() {
        return userId;
    }
}