#!/bin/bash

declare -a continuous_builds=(
  ':aws-lambdas:userprops-api-handlers:shadowJar'
)

tmux set -g pane-border-status top
tmux set -g pane-border-format " select with: ⌃b q #{pane_index}, running: #{pane_current_command} "
tmux set -g pane-base-index 1
tmux new-session -s "cdk-watch" -d -c "$PWD" 'cdk deploy --all --watch --no-rollback; '"$SHELL"
for continuous_build in "${continuous_builds[@]}"; do
  tmux split-window -h \
    -c "$PWD/../.." \
    './gradlew '"$continuous_build"' -t; '"$SHELL"
done
tmux split-window -v -c "$PWD/../.." 'printf "You are in %s\nTo exit, press Ctrl+b, &, y\n" "$(pwd)"; '"$SHELL"
tmux attach-session -d

# If you're using iTerm, open it and type the
# following command for iTerm to take over the tmux session:
# `tmux -CC attach`
