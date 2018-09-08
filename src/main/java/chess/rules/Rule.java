package chess.rules;

import chess.Action;
import chess.Board;

import java.io.Serializable;

public interface Rule extends Serializable {

  Rule MOVEMENT = new RuleMovement();
  Rule NO_OVERLAP = new RuleNoOverlap();
  Rule ATTACK_MOVE = new RuleAttackMove();
  Rule NO_CHANGE = new RuleNoChange();
  Rule KING_CASTLING = new RuleKingCastling();
  Rule EN_PASSANT = new RuleEnPassant();
  Rule PAWN_ATTACK = new RulePawnAttack();
  Rule PAWN_PROMOTION = new RulePawnPromotion();
  Rule NO_CHECK = new RuleNoCheck();

  boolean isActionAllowed(Board board, Action action);

  /**
   * A superior rule is a rule that makes all other rules redundant when passed.
   * They are usually used when the rule itself implements changes to the board when called upon.
   *
   * @return True or false.
   */
  boolean isSuperior();

}
