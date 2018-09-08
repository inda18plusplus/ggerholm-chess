package chess.rules;

import chess.Action;
import chess.Board;

public interface Rule {

  Rule MOVEMENT = new RuleMovement();
  Rule NO_OVERLAP = new RuleNoOverlap();
  Rule ATTACK_MOVE = new RuleAttackMove();
  Rule NO_CHANGE = new RuleNoChange();
  Rule KING_CASTLING = new RuleKingCastling();
  Rule EN_PASSANT = new RuleEnPassant();
  Rule PAWN_ATTACK = new RulePawnAttack();
  Rule PAWN_PROMOTION = new RulePawnPromotion();
  Rule NO_CHECK = new RuleNoCheck();

  /**
   * Returns whether or not an action is allowed according to the specific rule.
   * Superior rules that are passed may also alter the board during a call to this method.
   * If the action is not executed completely after passing a superior rule the board may
   * have to be reverted.
   *
   * @param board  The current board.
   * @param action The action to be executed.
   * @return True if the action passed the rule, otherwise false.
   */
  boolean isActionAllowed(Board board, Action action);

  /**
   * A superior rule is a rule that makes all other rules redundant when passed.
   * They are usually used when the rule itself implements changes to the board when called upon.
   *
   * @return True or false.
   */
  boolean isSuperior();

}
