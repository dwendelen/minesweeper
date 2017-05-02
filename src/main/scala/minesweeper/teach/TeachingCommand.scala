package minesweeper.teach

import minesweeper.game.{ExploreCommand, FlagCommand, MinesweeperCommand}
import minesweeper.util.Coordinate

abstract sealed class TeachingCommand(val output:List[Double]) {
    def createCommand(coordinateCurrentScenario: Coordinate): MinesweeperCommand
}

case class Skip() extends TeachingCommand(List(-1, 0)) {
    override def createCommand(c: Coordinate): MinesweeperCommand =
        null
}

case class Explore() extends TeachingCommand(List(1, -1)) {
    override def createCommand(c: Coordinate): MinesweeperCommand =
        ExploreCommand(c)
}

case class Flag() extends TeachingCommand(List(1, 1)) {
    override def createCommand(c: Coordinate): MinesweeperCommand =
        FlagCommand(c)
}

