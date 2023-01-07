#!/bin/bash

declare -r debug=0
declare -a continuous_builds=(
  #  ':aws-lambdas:clickup-api-handlers:shadowJar'
  #  ':aws-lambdas:userinfo-api-handlers:shadowJar'
  #  ':aws-lambdas:userprops-api-handlers:shadowJar'
)

function tmux() {
  local command=echo
  [ "${debug:-0}" -ne 0 ] || command=$(which tmux)
  "$command" "$@"
}

tmux set -g pane-border-status top
tmux set -g pane-border-format " select with: ⌃b q #{pane_index}, running: #{pane_current_command} "
tmux set -g pane-base-index 1
tmux new-session -s "cdk-watch" -d -c "$PWD" 'cdk deploy --all --watch --no-rollback; '"$SHELL"
#tmux split-window -h -c "$PWD/../.." 'printf "You are in %s\nTo exit, press Ctrl+b, &, y\n" "$(pwd)"; '"$SHELL"
for ((i = 0; i < "${#continuous_builds[@]}"; i++)); do
  task=${continuous_builds[$i]}
  tmux_args=('split-window')
  if [ "$i" -eq 0 ]; then
    tmux_args+=('-v')
  else
    tmux_args+=('-h')
  fi
  tmux_args+=('-c' "$PWD/../..")
  tmux_args+=('./gradlew '"$task"' -t; '"$SHELL")
  tmux "${tmux_args[@]}"
done
tmux attach-session -d

# If you're using iTerm, open it and type the
# following command for iTerm to take over the tmux session:
# `tmux -CC attach`
