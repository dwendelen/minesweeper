package minesweeper

import minesweeper.game.Minesweeper
import minesweeper.learn.DataSetExtractor
import minesweeper.store.Store
import minesweeper.ui._

object PlayUI {
    val NEURAL_NET = "/Users/daanw/neural/neuralnet_20.json"
    val store = new Store()

    val neuralNet = store.readNeuralNetFromFile(NEURAL_NET, () => throw new UnsupportedOperationException)
    val minesweeper = new Minesweeper(16, 16, 30)

    def main(args: Array[String]): Unit = {
        val ui = new UI(minesweeper, false, true)
        ui
                .init()
                .subscribe(click => click match {
                    case move: MoveEvent =>
                        doMove()
                    case click: ClickEvent =>

                    case send: SendEvent =>
                    case random: RandomEvent =>
                })
        //minesweeper.explore(8, 8)
    }


    def doMove(): Unit = {
        val points = DataSetExtractor.extractUsefullPoints(minesweeper)
        val actionPoint: (Int, Int, List[Double]) = points.map {
            case (x, y) =>
                val point = DataSetExtractor.extractDataPoint(minesweeper, x, y, Option.empty)

                (x, y, neuralNet.evaluate(point.inputs))

        }
                .minBy(p =>1/* Math.min(Math.abs(p._3 - 1), Math.abs(p._3 + 1))*/)

        if (actionPoint._3(0) > 0) {
            //minesweeper.explore(actionPoint._1, actionPoint._2)
        } else {
            //minesweeper.flag(actionPoint._1, actionPoint._2)
        }
    }
}
