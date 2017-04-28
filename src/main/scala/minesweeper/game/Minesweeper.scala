package minesweeper.game

import javax.swing.JButton

import rx.lang.scala.{Observable, Subscriber}

class Minesweeper(val height: Int, val width: Int, val nbOfMines: Int) {
    var block = false
    var cells: List[List[Cell]] = null

    reset()

    def reset(): Unit = {
        block = false
        cells = (0 until width).map(_ => {
            (0 until height).map(_ => {
                new Cell()
            }).toList
        }).toList
        makeBombs(nbOfMines)
    }

    def makeBombs(nbOfBombsToDo: Int): Unit = nbOfBombsToDo match {
        case 0 =>
        case _ =>
            val x = (Math.random() * width).floor.toInt
            val y = (Math.random() * height).floor.toInt
            val cell = cells(x)(y)
            if (cell.number == Cell.BOMB) {
                makeBombs(nbOfBombsToDo)
            } else {
                cell.number = Cell.BOMB
                getNeighbours(x, y)
                        .map { case (xc: Int, yc: Int) =>
                            cells(xc)(yc)
                        }
                        .filter(!_.bomb)
                        .foreach(_.number += 1)
                makeBombs(nbOfBombsToDo - 1)
            }
    }

    var subscriber: Subscriber[MinesweeperEvent] = null

    def observable(): Observable[MinesweeperEvent] = {
        Observable.apply((sub) => {
            subscriber = sub
        })
    }

    def setButton(x: Int, y: Int, button: JButton): Unit = {
        cells(x)(y).button = button
    }

    def click(x: Int, y: Int): Unit = {
        doIfClickable(x, y, cell => {
            cell.exposed = true
            notify(ExposedEvent(cell))

            cell.number match {
                case 0 => clickOpen(x, y)
                case Cell.BOMB =>
                    block = true
                    notify(LostEvent())
                case _ =>
            }
            if (playerHasWon()) {
                notify(WonEvent())
            }
        })
    }

    def flag(x: Int, y: Int): Unit = {
        doIfClickable(x, y, cell => {
            cell.flag = true
            notify(FlaggedEvent(cell))
        })
    }

    def doIfClickable(x: Int, y: Int, todo: Cell => Unit): Unit = {
        if (block) {
            return
        }

        val cell = cells(x)(y)

        if (!cell.clickable) {
            return
        }
    }

    def clickOpen(x: Int, y: Int): Unit = {
        getNeighbours(x, y).foreach {
            case (xn, yn) => click(xn, yn)
        }
    }

    def getNeighbours(x: Int, y: Int): List[(Int, Int)] = {
        val grid =
            ((x - 1) to (x + 1)).flatMap(x =>
                ((y - 1) to (y + 1)).map(y => (x, y))
            )
        grid
            .filter(_ != (x, y))
            .filter(coordinate => withinField(coordinate._1, coordinate._2))
            .toList
    }

    def withinField(x: Int, y: Int): Boolean = {
        x >= 0 &&
                x < width &&
                y >= 0 &&
                y < height
    }

    def playerHasWon(): Boolean = {
        cells
                .flatten
                .exists(p => !p.bomb && p.clickable)
    }

    def notify(event: MinesweeperEvent): Unit = {
        if (subscriber != null) {
            subscriber.onNext(event)
        }
    }
}