package com.souha.service;

import com.souha.model.Commit;
import com.souha.model.GitHistory;
import com.souha.model.StatUtilisateur;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitAnalyseur {

    private Map<Integer, StatUtilisateur> statsByUserId = new HashMap<>();
    private Map<String, Integer> authorToId = new HashMap<>();
    private List<Commit> allCommits = new ArrayList<>();
    private GitHistory history = new GitHistory();
    private int nextId = 1;

    public Map<Integer, StatUtilisateur> analyser(String repoPath) throws Exception {

        Git git = Git.open(new File(repoPath));
        Repository repo = git.getRepository();

        DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
        df.setRepository(repo);
        df.setDetectRenames(true);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Iterable<RevCommit> commits = git.log().all().call();

        for (RevCommit commit : commits) {
            String author = commit.getAuthorIdent().getName();
            String date   = sdf.format(commit.getAuthorIdent().getWhen());
            String hash   = commit.getName();

            if (!authorToId.containsKey(author)) {
                authorToId.put(author, nextId);
                statsByUserId.put(nextId, new StatUtilisateur(nextId, author));
                nextId++;
            }

            int userId = authorToId.get(author);
            int added = 0, deleted = 0, files = 0;

            if (commit.getParentCount() > 0) {
                RevCommit parent = commit.getParent(0);
                List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());
                files = diffs.size();

                for (DiffEntry entry : diffs) {
                    for (Edit edit : df.toFileHeader(entry).toEditList()) {
                        added   += edit.getEndB() - edit.getBeginB();
                        deleted += edit.getEndA() - edit.getBeginA();
                    }
                }
            }

            statsByUserId.get(userId).commitAdd(added, deleted, files, date);

            Commit c = new Commit(hash, userId, author, date, added, deleted, files);
            allCommits.add(c);
            history.addCommit(c);
        }

        df.close();
        git.close();
        return statsByUserId;
    }

    public GitHistory getHistory()              { return history; }
    public List<Commit> getAllCommits()          { return allCommits; }
    public Map<String, Integer> getAuthorToId() { return authorToId; }
}