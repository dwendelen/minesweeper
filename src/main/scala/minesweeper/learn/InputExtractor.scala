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
    def extractExplorablesWithExposedNeighbours() : List[Coordinate] = {
        extractExplorables()
            .filter(hasExposedNeighbour)
    }

    def hasExposedNeighbour(coordinate: Coordinate): Boolean = {
        val neighbours = Coordinate.tabulate(coordinate, 1)
        neighbours
            .flatten
            .map(minesweeper.cellAt)
            .filter(_ != null)
            .exists(c => c.exposed)
    }
    def extractExplorables(): List[Coordinate] = {
        List.tabulate(minesweeper.width, minesweeper.height)((x, y) => Coordinate(x, y))
                .flatten
                .filter(c => {
                    val cell = minesweeper.cellAt(c)
                    cell.explorable
                })
    }

    def extractArea(coordinate: Coordinate): Grid[Cell] = {
        val cells = Coordinate.tabulate(coordinate, 2)
                .map(_.map(
                    minesweeper.cellAt
                ))
        Grid(cells)
    }

    def random() : Coordinate = {
        val interesting = extractExplorables()
        val i = (Math.random() * interesting.size).floor.toInt
        interesting(i)
    }
}
