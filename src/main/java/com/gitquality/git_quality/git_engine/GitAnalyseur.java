package com.gitquality.git_quality.git_engine;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitAnalyseur {

    public Map<String, StatUtilisateur> analyser(String repoPath) throws Exception {
        Map<String, StatUtilisateur> stats = new HashMap<>();
        Git git;

        // Gérer les repos distants ou locaux
        if (repoPath.startsWith("http") || repoPath.startsWith("git@")) {
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "git-stats-temp");
            if (tempDir.exists()) deleteFolder(tempDir);
            git = Git.cloneRepository().setURI(repoPath).setDirectory(tempDir).call();
        } else {
            git = Git.open(new File(repoPath));
        }

        Repository repo = git.getRepository();
        DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
        df.setRepository(repo);
        df.setDetectRenames(true);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Iterable<RevCommit> commits = git.log().all().call();

        for (RevCommit commit : commits) {
            String author = commit.getAuthorIdent().getName();
            String date   = sdf.format(commit.getAuthorIdent().getWhen());

            int adds = 0, del = 0, files = 0;
            if (commit.getParentCount() > 0) {
                RevCommit parent = commit.getParent(0);
                List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());
                files = diffs.size();
                for (DiffEntry entry : diffs) {
                    for (Edit edit : df.toFileHeader(entry).toEditList()) {
                        adds += edit.getEndB() - edit.getBeginB();
                        del += edit.getEndA() - edit.getBeginA();
                    }
                }
            }
            stats.putIfAbsent(author, new StatUtilisateur(author));
            stats.get(author).commitAdd(adds, del, files, date);
        }

        df.close();
        git.close();
        return stats;
    }

    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) deleteFolder(f);
                else f.delete();
            }
        }
        folder.delete();
    }
}