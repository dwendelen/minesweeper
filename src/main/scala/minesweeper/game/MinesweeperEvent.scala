package minesweeper.game

abstract sealed class MinesweeperEvent()
case class GameResetEvent() extends MinesweeperEvent
case class LostEvent() extends MinesweeperEvent
case class WonEvent() extends MinesweeperEvent
case class ExposedEvent(number: Int, coordinate: Coordinate) extends MinesweeperEvent
case class FlaggedEvent(coordinate: Coordinate) extends MinesweeperEvent
case class UnflaggedEvent(coordinate: Coordinate) extends MinesweeperEvent