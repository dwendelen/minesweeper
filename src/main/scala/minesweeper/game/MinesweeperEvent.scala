package minesweeper.game

abstract sealed class MinesweeperEvent()

case class GameResetEvent() extends MinesweeperEvent

case class LostEvent() extends MinesweeperEvent

case class WonEvent() extends MinesweeperEvent

case class ExposedEvent(cell: Cell) extends MinesweeperEvent

case class FlaggedEvent(cell: Cell) extends MinesweeperEvent

case class UnflaggedEvent(cell: Cell) extends MinesweeperEvent