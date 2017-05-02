package minesweeper.game

import minesweeper.util.{Coordinate, Grid}
import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject

class Minesweeper(val height: Int, val width: Int, val nbOfMines: Int) {
    var cells: Grid[Cell] = null
    private val subject = PublishSubject[MinesweeperEvent]()
    private var blocked = false

    def reset(): Unit = {
        cells = Grid.tabulate(width, height)((x, y) => new Cell(Coordinate(x, y)))
        makeBombs(nbOfMines)
        blocked = false
        subject.onNext(GameResetEvent())
    }

    private def makeBombs(nbOfBombsToDo: Int): Unit = nbOfBombsToDo match {
        case 0 =>
        case _ =>
            val x = (Math.random() * width).floor.toInt
            val y = (Math.random() * height).floor.toInt
            val potentialNewBomb = cells(x,y)
            if (potentialNewBomb.number == Cell.BOMB) {
                makeBombs(nbOfBombsToDo)
            } else {
                potentialNewBomb.number = Cell.BOMB
                getNeighbours(Coordinate(x, y))
                        .map(cellAt)
                        .filter(!_.bomb)
                        .foreach(_.number += 1)
                makeBombs(nbOfBombsToDo - 1)
            }
    }

    reset()

    def cellAt(coordinate: Coordinate): Cell = {
        if (withinField(coordinate)) {
            cells(coordinate)
        } else {
            null
        }
    }

    def observable(): Observable[MinesweeperEvent] = {
        subject
    }

    def execute(cmd: MinesweeperCommand): Unit = {
        if (blocked) {
            return
        }
        val cell = cellAt(cmd.coordinate)
        if (cell.exposed != cmd.expectsExposed) {
            return
        }
        cmd match {
            case _: ExploreCommand =>
                exploreUnexposed(cell)
            case _: FlagCommand =>
                if (!cell.flag) {
                    cell.flag = true
                    subject.onNext(FlaggedEvent(cell))
                }
            case _: UnflagCommand =>
                if (cell.flag) {
                    cell.flag = false
                    subject.onNext(UnflaggedEvent(cell))
                }
            case _: ExploreNeighboursCommand =>
                val nbOfFlagsAround = getNeighbours(cmd.coordinate)
                        .count(cellAt(_).flag)
                if (nbOfFlagsAround == cell.number) {
                    exploreNeighbours(cmd.coordinate)
                }
        }
    }

    private def explore(coordinate: Coordinate): Unit = {
        execute(ExploreCommand(coordinate))
    }

    private def exploreUnexposed(unexposedCell: Cell): Unit = {
        if (unexposedCell.flag) {
            return
        }

        unexposedCell.exposed = true
        subject.onNext(ExposedEvent(unexposedCell))
        unexposedCell.number match {
            case 0 => exploreNeighbours(unexposedCell.coordinate)
            case Cell.BOMB =>
                blocked = true
                subject.onNext(LostEvent())
            case _ =>
        }
        if (playerHasWon()) {
            blocked = true
            subject.onNext(WonEvent())
        }
    }

    private def exploreNeighbours(coordinate: Coordinate): Unit = {
        getNeighbours(coordinate).foreach(explore)
    }

    private def getNeighbours(coor: Coordinate): List[Coordinate] = {
        Coordinate.tabulate(coor, 1)
                .flatten
                .filter(_ != coor)
                .filter(coordinate => withinField(coordinate))
    }

    def withinField(coordinate: Coordinate): Boolean = {
        val x = coordinate.x
        val y = coordinate.y
        x >= 0 &&
                x < width &&
                y >= 0 &&
                y < height
    }

    def playerHasWon(): Boolean = {
        val hasMovesLeft = cells
                .exists(p => !p.exposed && !p.bomb)
        !hasMovesLeft
    }
}