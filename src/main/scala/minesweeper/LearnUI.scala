package minesweeper

import minesweeper.learn.{DataSet, LearningProcess}
import minesweeper.neural.NeuralNetwork
import minesweeper.store.Store
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.IOScheduler

import scala.concurrent.duration.Duration

object LearnUI {
    val DATA_SET = "/Users/daanw/neural/dataset.json"
    val NEURAL_NET = "/Users/daanw/neural/neuralnet.json"

    def main(args: Array[String]): Unit = {
        val store = new Store()

        val dataSet = store.readDataSetFromFile(DATA_SET, () => DataSet(List()))
        val neuralNet = store.readNeuralNetFromFile(NEURAL_NET, () => NeuralNetwork.createRandom(48, List(100, 100, 100)))

        val process = new LearningProcess(0.1, dataSet, neuralNet)

        Observable.interval(Duration(1, scala.concurrent.duration.MINUTES))
                .observeOn(IOScheduler.apply())
                .map(_ => store.writeToFile(process.neuralNetwork, NEURAL_NET))
                .subscribe()

        while (true) {
            process.tick()
        }
    }
}
