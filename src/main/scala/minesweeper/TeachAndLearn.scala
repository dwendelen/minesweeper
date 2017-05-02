package minesweeper

import minesweeper.Config._
import minesweeper.game.Minesweeper
import minesweeper.learn.{DataSet, LearningProcess}
import minesweeper.store.Store
import minesweeper.teach.TeachingSession
import minesweeper.ui.TeachingWindow
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.IOScheduler

import scala.concurrent.duration.Duration

object TeachAndLearn {
    val store = new Store()

    def main(arg: Array[String]): Unit = {
        val minesweeper = new Minesweeper(16, 16, 30)

        val dataSet = store.readDataSetFromFile(DATA_SET, () => DataSet(List()))
        val learningProcess = setUpLearingProcess(dataSet)
        setUpTeachingSession(minesweeper, dataSet)

        while (true) {
            learningProcess.tick()
        }
    }

    private def setUpTeachingSession(minesweeper: Minesweeper, dataSet: DataSet): Unit = {
        val teachingSession = new TeachingSession(minesweeper)
        val teachingWindow = new TeachingWindow()
        teachingWindow.controlAndObserverTeachingSession(teachingSession)
        teachingWindow.run()
        teachingSession.start()

        teachingSession.observeNewDataPoints()
                .subscribe(dataPoint => {
                    dataSet.add(dataPoint)
                    store.writeToFile(dataSet, DATA_SET)
                })
    }

    private def setUpLearingProcess(dataSet: DataSet) = {
        val neuralNet = store.readNeuralNetFromFile(NEURAL_NET_1, INITIAL_NETWORK_FACTORY)
        val learningProcess = new LearningProcess(STEP_FACTOR, dataSet, neuralNet, 0)
        startAutosave(learningProcess)

        learningProcess
    }

    private def startAutosave(learningProcess: LearningProcess) = {
        Observable.interval(Duration(10, scala.concurrent.duration.SECONDS))
                .observeOn(IOScheduler.apply())
                .map(_ => store.writeToFile(learningProcess.neuralNetwork, NEURAL_NET_1))
                .subscribe()
    }

    def autosaveDataset(teachingSession: TeachingSession): Unit = {
        teachingSession.observeScenarios()
                .subscribe()
    }
}
