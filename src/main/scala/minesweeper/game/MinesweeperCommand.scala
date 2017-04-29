package minesweeper.game

sealed abstract class MinesweeperCommand(val coordinate: Coordinate, val expectsExposed: Boolean = false)
case class ExploreCommand(c: Coordinate) extends MinesweeperCommand(c)
case class FlagCommand(c: Coordinate) extends MinesweeperCommand(c)
case class UnflagCommand(c: Coordinate) extends MinesweeperCommand(c)
case class ExploreNeighboursCommand(c: Coordinate) extends MinesweeperCommand(c, true)
