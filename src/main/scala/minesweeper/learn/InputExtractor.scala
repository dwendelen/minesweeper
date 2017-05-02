package minesweeper.learn

import minesweeper.game.{Cell, Minesweeper}
import minesweeper.util.{Coordinate, Grid}

object InputExtractor {
    def mapToInput(grid: Grid[Cell]): List[Double] = {
        grid.toList()
                .flatMap(cell => {
                    if (cell == null)
                        List(0d, 0d, 0d, 0d)
                    else
                        List(cell.number.toDouble / 4.0d, toDouble(cell.exposed), toDouble(cell.flag), 1d)
                })
    }

    def toDouble(boolean: Boolean): Double = if (boolean) 1d else 0d
}

class InputExtractor(minesweeper: Minesweeper) {
    def extractExplorables(): List[Coordinate] = {
        List.tabulate(minesweeper.width, minesweeper.height)((x, y) => Coordinate(x, y))
                .flatten
                .filter(c => {
                    val cell = minesweeper.cellAt(c)
                    cell.explorable
                })
    }

    def extractArea(coordinate: Coordinate): List[List[Cell]] = {
        Coordinate.tabulate(coordinate, 2)
                .map(_.map(
                    minesweeper.cellAt
                ))
    }

    def random() : Coordinate = {
        val interesting = extractExplorables()
        val i = (Math.random() * interesting.size).floor.toInt
        interesting(i)
    }
}
