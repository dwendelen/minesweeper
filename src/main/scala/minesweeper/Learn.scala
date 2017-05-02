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
            val error1 = learningProcess._1.tick()
            val error2 = learningProcess._2.tick()
            if(Math.random() < 0.001) {
                    println(error1, error2)
            }
        }
    }

    private def setUpLearningProcess(dataSet: DataSet) = {
        val neuralNet_1 = store.readNeuralNetFromFile(NEURAL_NET_1, INITIAL_NETWORK_FACTORY)
        val neuralNet_2 = store.readNeuralNetFromFile(NEURAL_NET_2, INITIAL_NETWORK_FACTORY)
        val learningProcess_1 = new LearningProcess(Config.STEP_FACTOR, dataSet, neuralNet_1, 0)
        val learningProcess_2 = new LearningProcess(Config.STEP_FACTOR, dataSet, neuralNet_2, 1)
        startAutosave(learningProcess_1, NEURAL_NET_1)
        startAutosave(learningProcess_2, NEURAL_NET_2)

        (learningProcess_1, learningProcess_2)
    }

    private def startAutosave(learningProcess: LearningProcess,  file:String) = {
        Observable.interval(Duration(10, scala.concurrent.duration.SECONDS))
                .observeOn(IOScheduler.apply())
                .map(_ => store.writeToFile(learningProcess.neuralNetwork, file))
                .subscribe()
    }

    def autosaveDataset(teachingSession: TeachingSession) :Unit = {
        teachingSession.observeScenarios()
                .subscribe()
    }
}
