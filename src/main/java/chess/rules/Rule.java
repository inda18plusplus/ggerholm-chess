package chess.rules;

import chess.Action;
import chess.Board;

public interface Rule {

  enum Result {
    Passed, NotPassed, Invalid
  }

  Rule MOVEMENT = new RuleMovement();
  Rule NO_OVERLAP = new RuleNoOverlap();
  Rule ATTACK = new RuleAttack();
  Rule NO_CHANGE = new RuleNoChange();
  Rule KING_CASTLING = new RuleKingCastling();
  Rule EN_PASSANT = new RuleEnPassant();
  Rule PAWN_ATTACK = new RulePawnAttack();
  Rule PAWN_PROMOTION = new RulePawnPromotion();
  Rule NO_CHECK = new RuleNoCheck();
  Rule KING_INVULNERABILITY = new RuleKingInvulnerability();

  /**
   * Returns whether or not an action is allowed according to the specific rule.
   * Superior rules that are passed may also add further acts to the action,
   * which will be executed along with the original ones.
   *
   * @param board  The current board.
   * @param action The action to be executed.
   * @return True if the action passed the rule, otherwise false.
   */
  Result isActionAllowed(Board board, Action action);

  /**
   * A superior rule is a rule that makes all other rules redundant when passed.
   * They are usually used when the rule itself adds further acts to the action.
   *
   * @return True or false.
   */
  boolean isSuperior();

}
