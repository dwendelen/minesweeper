package minesweeper

import javax.swing.JButton

import minesweeper.game.Minesweeper
import minesweeper.learn.{DataSet, DataSetExtractor, LearningProcess}
import minesweeper.neural.NeuralNetwork
import minesweeper.store.Store
import minesweeper.ui.{ClickEvent, RandomEvent, SendEvent, UI}
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.IOScheduler

import scala.concurrent.duration.Duration

object LearnUI {
    val DATA_SET = "/Users/daanw/neural/dataset.json"
    val NEURAL_NET = "/Users/daanw/neural/neuralnet_20.json"

    var pendingClickEvents: List[PendingEvent] = List()

    val store = new Store()

    val dataSet = store.readDataSetFromFile(DATA_SET, () => DataSet(List()))
    val neuralNet = store.readNeuralNetFromFile(NEURAL_NET, () => NeuralNetwork.createRandom(48, List(20)))

    val process = new LearningProcess(0.01, dataSet, neuralNet)

    Observable.interval(Duration(10, scala.concurrent.duration.SECONDS))
            .observeOn(IOScheduler.apply())
            .map(_ => store.writeToFile(process.neuralNetwork, NEURAL_NET))
            .subscribe()

    val minesweeper = new Minesweeper(16, 16, 30)


    def main(args: Array[String]): Unit = {

        val ui = new UI(minesweeper, true)
        ui
                .init()
                .subscribe(click => click match {
                    case click: ClickEvent =>
                        val pending = findPending(click.x, click.y)
                        if (pending.isEmpty) {
                            val pendingE = PendingClickEvent(click.x, click.y, click.button)
                            click.button.setText("x")
                            pendingClickEvents = pendingE :: pendingClickEvents
                        } else {
                            pending.get match {
                                case c: PendingClickEvent =>
                                    pendingClickEvents = pendingClickEvents.filter(_ != c)
                                    val pendingE = PendingFlagEvent(click.x, click.y, click.button)
                                    click.button.setText("V")
                                    pendingClickEvents = pendingE :: pendingClickEvents
                                case f: PendingFlagEvent =>
                                    pendingClickEvents = pendingClickEvents.filter(_ != f)
                                    click.button.setText("")
                            }
                        }
                    case send: SendEvent =>
                        flushPending()
                    case random: RandomEvent =>
                        val clickable = DataSetExtractor.extractUsefullPoints(minesweeper)
                        val index = Math.random() * clickable.size
                        val coordinate = clickable(index.floor.toInt)
                        //minesweeper.explore(coordinate)
                })

        while (true) {
            process.tick()
        }
    }


    def flushPending(): Unit = {
        val coordinates = pendingClickEvents.map(e => (e.x, e.y))

        //        val dataPoints = pendingClickEvents.map(e =>{
        //            DataSetExtractor.extractDataPoint(minesweeper, e.x, e.y, Option(e))
        //        })
        val dataPoints = DataSetExtractor.extractUsefullPoints(minesweeper)
                .map {
                    case (x, y) =>
                        val event = findPending(x, y)
                        DataSetExtractor.extractDataPoint(minesweeper, x, y, event)
                }

        process.addDataPoints(dataPoints)
        store.writeToFile(process.dataSet, DATA_SET)

        pendingClickEvents.foreach(
            //case e: PendingClickEvent => minesweeper.explore(e.x, e.y)
            //case e: PendingFlagEvent => minesweeper.flag(e.x, e.y)
            _ =>println( "stuff")
        )
        pendingClickEvents = List()
    }

    private def findPending(x: Int, y: Int) = {
        pendingClickEvents
                .find(e => e.x == x && e.y == y)
    }
}

sealed class PendingEvent(val x: Int, val y: Int, val button: JButton)
case class PendingClickEvent(x1: Int, y1: Int, button1: JButton) extends PendingEvent(x1, y1, button1)
case class PendingFlagEvent(x1: Int, y1: Int, button1: JButton) extends PendingEvent(x1, y1, button1)