package minesweeper

import javax.swing.JButton

import minesweeper.game.Minesweeper
import minesweeper.learn.{DataPoint, DataSet, LearningProcess}
import minesweeper.neural.NeuralNetwork
import minesweeper.store.Store
import minesweeper.ui.{ClickEvent, SendEvent, UI}
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.IOScheduler

import scala.concurrent.duration.Duration

object LearnUI {
    val DATA_SET = "/Users/daanw/neural/dataset.json"
    val NEURAL_NET = "/Users/daanw/neural/neuralnet.json"

    var pendingClickEvents: List[PendingClickEvent] = List()

    val store = new Store()

    val dataSet = store.readDataSetFromFile(DATA_SET, () => DataSet(List()))
    val neuralNet = store.readNeuralNetFromFile(NEURAL_NET, () => NeuralNetwork.createRandom(48, List(20, 20, 20)))

    val process = new LearningProcess(0.1, dataSet, neuralNet)

    Observable.interval(Duration(1, scala.concurrent.duration.MINUTES))
            .observeOn(IOScheduler.apply())
            .map(_ => store.writeToFile(process.neuralNetwork, NEURAL_NET))
            .subscribe()

    val minesweeper = new Minesweeper(16, 16, 30)


    def main(args: Array[String]): Unit = {

        new UI(minesweeper)
                .init()
                .subscribe(click => click match {
                    case click: ClickEvent =>
                        val pendingE = PendingClickEvent(click.x, click.y, click.button)
                        click.button.setText("1")
                        pendingClickEvents = pendingE :: pendingClickEvents
                    case send: SendEvent =>
                        flushPending()
                })

        while (true) {
            process.tick()
        }
    }

    def extractDataPoint(x: Int, y: Int): DataPoint = {
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
                    0d
                } else {
                    val cell = minesweeper.cells(xi)(yi)
                    if (!cell.exposed) {
                        -1d
                    } else {
                        cell.number
                    }
                }
        }
        DataPoint(input, 1)
    }

    def flushPending(): Unit = {
        val allPoints: List[(Int, Int)] =
            (0 until minesweeper.width).flatMap(x => {
                (0 until minesweeper.height).map(y => {
                    (x, y)
                })
            })
                    .toList
        val dataPoints = allPoints.map {
            case (x, y) =>  extractDataPoint(x, y)
        }

        process.addDataPoints(dataPoints)
        store.writeToFile(process.dataSet, DATA_SET)

        pendingClickEvents.foreach(e => {
            e.button.setText("")
            minesweeper.click(e.x, e.y)
        })
    }
}

case class PendingClickEvent(x: Int, y: Int, button: JButton)
