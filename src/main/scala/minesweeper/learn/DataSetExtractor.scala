package minesweeper.learn

import minesweeper.game.Minesweeper
import minesweeper.{PendingClickEvent, PendingEvent, PendingFlagEvent}


object DataSetExtractor {
    def extractDataPoint(minesweeper: Minesweeper, x: Int, y: Int, event: Option[PendingEvent]): DataPoint = {
        val area = ((x - 3) to (x + 3))
                .flatMap(xi => {
                    ((y - 3) to (y + 3))
                            .map(yi => (xi, yi))
                })
                .filter { case (xi, yi) =>
                    !(xi == x && yi == y)
                }
                .toList
        val input: List[Double] = area.map {
            case (xi, yi) =>
                if (xi < 0 || xi >= minesweeper.width || yi < 0 || yi >= minesweeper.height) {
                    -1d
                } else {
                    val cell = minesweeper.cells(xi)(yi)
                    if (cell.flag) {
                        -5d
                    } else if (!cell.exposed) {
                        -1d
                    } else {
                        cell.number
                    }
                }
        }
        val value = if (event.isDefined) {
            event.get match {
                case e: PendingClickEvent => 1d
                case e: PendingFlagEvent => -1d
            }
        } else {
            0d
        }
        DataPoint(input, value)
    }

    def extractUsefullPoints(minesweeper: Minesweeper): List[(Int, Int)] = {
        val allPoints: List[(Int, Int)] =
            (0 until minesweeper.width).flatMap(x => {
                (0 until minesweeper.height).map(y => {
                    (x, y)
                })
            }).toList
        allPoints.filter {
            case (x, y) => !(minesweeper.cells(x)(y).exposed || minesweeper.cells(x)(y).flag)
        }
    }
}
