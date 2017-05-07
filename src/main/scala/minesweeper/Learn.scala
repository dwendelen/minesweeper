package minesweeper

import Config._
import minesweeper.game.Minesweeper
import minesweeper.learn.{DataSet, LearningProcess}
import minesweeper.neural.NeuralNetwork
import minesweeper.store.Store
import minesweeper.teach.TeachingSession
import minesweeper.ui.TeachingWindow
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.IOScheduler

import scala.concurrent.duration.Duration

object Learn {
    val store = new Store()

    def main(arg: Array[String]): Unit = {
        val minesweeper = new Minesweeper(16, 16, 30)

        val dataSet = store.readDataSetFromFile(DATA_SET, () => DataSet(List()))
        val learningProcess = setUpLearningProcess(dataSet)

        while (true) {
            val error = learningProcess.tick()
            if (Math.random() < 0.001) {
                println(error)
            }
        }
    }

    private def setUpLearningProcess(dataSet: DataSet) = {
        val neuralNets = store.readNeuralNetFromFile(NEURAL_NET, INITIAL_NETWORK_FACTORIES)
        val learningProcess = new LearningProcess(List(STEP_FACTOR, STEP_FACTOR), dataSet, neuralNets)
        startAutosave(learningProcess, NEURAL_NET)

        learningProcess
    }

    private def startAutosave(learningProcess: LearningProcess, file: String) = {
        Observable.interval(Duration(10, scala.concurrent.duration.SECONDS))
            .observeOn(IOScheduler.apply())
            .map(_ => store.writeToFile(learningProcess.neuralNetworks, file))
            .subscribe()
    }

    def autosaveDataset(teachingSession: TeachingSession): Unit = {
        teachingSession.observeScenarios()
            .subscribe()
    }
}
