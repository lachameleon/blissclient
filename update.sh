#!/bin/bash
# This shell script is vibe coded !!
# ===== CONFIG =====
UPSTREAM_REMOTE="upstream"                        # Meteor remote
UPSTREAM_BRANCH="master"                          # Meteor branch
GITHUB_REMOTE="origin"                            # Your GitHub repo
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD) # current branch name

# ===== STEP 0: sanity check =====
if ! git rev-parse --git-dir > /dev/null 2>&1; then
    echo "Not a git repository!"
    exit 1
fi

# ===== STEP 1: commit everything as a failsafe =====
git add .
git commit -m "WIP: save all local changes before upstream merge" || echo "No changes to commit"

# ===== STEP 2: fetch upstream =====
git fetch $UPSTREAM_REMOTE

# ===== STEP 3: merge upstream into current branch =====
git merge --no-ff $UPSTREAM_REMOTE/$UPSTREAM_BRANCH -m "Merge $UPSTREAM_REMOTE/$UPSTREAM_BRANCH into $CURRENT_BRANCH"

# ===== STEP 4: push to GitHub =====
git push $GITHUB_REMOTE $CURRENT_BRANCH

echo "✅ Done! Your local changes are committed, Meteor merged, and pushed to GitHub."
