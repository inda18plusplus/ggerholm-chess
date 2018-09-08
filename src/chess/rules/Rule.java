package chess.rules;

import chess.Action;
import chess.Board;

public interface Rule {

  Rule MOVEMENT = new RuleMovement();
  Rule NO_OVERLAP = new RuleNoOverlap();
  Rule ATTACK_MOVE = new RuleAttackMove();

  boolean isActionAllowed(Board board, Action action);

}
