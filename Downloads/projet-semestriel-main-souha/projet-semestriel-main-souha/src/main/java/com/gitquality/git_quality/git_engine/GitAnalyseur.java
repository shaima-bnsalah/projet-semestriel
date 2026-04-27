package com.gitquality.git_quality.git_engine;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class GitAnalyseur {

    public Map<String, StatUtilisateur> analyser(String repoPath) throws Exception {
        Map<String, StatUtilisateur> stats = new HashMap<>();
        Git git;

        if (repoPath.startsWith("http")) {
            String uniqueName = "git-stats-" + System.currentTimeMillis();
            File tempDir = new File(System.getProperty("java.io.tmpdir"), uniqueName);
            git = Git.cloneRepository().setURI(repoPath).setDirectory(tempDir).setCloneAllBranches(true).call();
        } else {
            git = Git.open(new File(repoPath));
        }

        Repository repo = git.getRepository();
        
        Map<String, String> commitToBranch = new HashMap<>();
        List<Ref> branches = git.branchList().setListMode(org.eclipse.jgit.api.ListBranchCommand.ListMode.ALL).call();
        for (Ref br : branches) {
            String bName = br.getName().replace("refs/heads/", "").replace("refs/remotes/origin/", "");
            Iterable<RevCommit> brCommits = git.log().add(br.getObjectId()).call();
            for (RevCommit c : brCommits) commitToBranch.putIfAbsent(c.getName(), bName);
        }

        DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
        df.setRepository(repo);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Iterable<RevCommit> commits = git.log().all().call();

        for (RevCommit commit : commits) {
            String author = commit.getAuthorIdent().getName();
            String date   = sdf.format(commit.getAuthorIdent().getWhen());
            String msg    = commit.getShortMessage();
            String branch = commitToBranch.getOrDefault(commit.getName(), "main");

            int adds = 0, del = 0, files = 0;
            if (commit.getParentCount() > 0) {
                List<DiffEntry> diffs = df.scan(commit.getParent(0).getTree(), commit.getTree());
                files = diffs.size();
                for (DiffEntry entry : diffs) {
                    for (Edit edit : df.toFileHeader(entry).toEditList()) {
                        adds += edit.getEndB() - edit.getBeginB();
                        del  += edit.getEndA() - edit.getBeginA();
                    }
                }
            }

            stats.putIfAbsent(author, new StatUtilisateur(author));
            // 🟢 On utilise la nouvelle méthode avec msg et branch
            stats.get(author).commitAdd(adds, del, files, date, msg, branch);
        }

        df.close();
        git.close();
        return stats;
    }
}