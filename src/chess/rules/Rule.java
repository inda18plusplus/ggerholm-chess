package chess.rules;

import chess.Action;
import chess.Board;

public interface Rule {

  boolean isActionAllowed(Board board, Action action);

}
