package minesweeper.game

import javax.swing.JButton

import rx.lang.scala.{Observable, Subscriber}

class Minesweeper(val height: Int, val width: Int, val nbOfMines: Int) {
    var block = false
    var cells: List[List[Cell]] = null

    reset()

    def reset(): Unit = {
        block = false
        cells = (0 until width)
                .map(_ => {
                    val cells = (0 until height)
                            .map(_ => {
                                val cell = new Cell()
                                cell.number = 0
                                cell
                            })
                            .toList
                    cells
                }).toList
        makeBomb(nbOfMines)
    }


    def makeBomb(todo: Int): Unit = todo match {
        case 0 =>
        case _ =>
            val x = (Math.random() * width).floor.toInt
            val y = (Math.random() * height).floor.toInt
            val cell = cells(x)(y)
            if (cell.number == -1) {
                makeBomb(todo)
            } else {
                cell.number = -1
                getNeighbours(x, y)
                        .map { case (xc: Int, yc: Int) =>
                            cells(xc)(yc)
                        }
                        .filter(_.number != -1)
                        .foreach(_.number += 1)
                makeBomb(todo - 1)
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
        if (block) {
            return
        }

        val cell = cells(x)(y)

        if (cell.exposed) {
            return
        }
        cell.exposed = true

        if (subscriber != null) {
            subscriber.onNext(OpenedEvent(cell))
        }

        cell.number match {
            case 0 => clickOpen(x, y)
            case -1 =>
                if (subscriber != null) {
                    subscriber.onNext(LostEvent())
                }
                block = true
            case _ =>
        }
        if (hasWon()) {
            if (subscriber != null) {
                subscriber.onNext(WonEvent())
            }
        }
    }

    def clickOpen(xi: Int, yi: Int): Unit = {
        getNeighbours(xi, yi).foreach {
            case (x, y) => click(x, y)
        }
    }

    def getNeighbours(x: Int, y: Int): List[(Int, Int)] = {
        val r = ((x - 1) to (x + 1))
                .flatMap(x =>
                    ((y - 1) to (y + 1))
                            .map(y => (x, y))
                )
                .filter {
                    case (xi, yi) =>
                        if (x == xi && y == yi) {
                            false
                        } else {
                            xi >= 0 && xi < width && yi >= 0 && yi < height
                        }
                }
                .toList
        r
    }

    def hasWon(): Boolean = {
        val won = cells.flatten
                .filter(_.number != -1)
                .filter(!_.exposed)
                .isEmpty
        won

    }
}

class Cell() {
    var exposed = false
    var button: JButton = _
    var number: Int = 0
}

abstract sealed class MinesweeperEvent()

case class LostEvent() extends MinesweeperEvent

case class WonEvent() extends MinesweeperEvent

case class OpenedEvent(cell: Cell) extends MinesweeperEvent