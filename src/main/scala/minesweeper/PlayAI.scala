package minesweeper

import minesweeper.Config._
import minesweeper.game.{ExploreCommand, FlagCommand, Minesweeper}
import minesweeper.learn.{DataSet, InputExtractor, LearningProcess}
import minesweeper.store.Store
import minesweeper.teach.TeachingSession
import minesweeper.ui.PlayWindow
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.IOScheduler

import scala.concurrent.duration.Duration

object PlayAI {
    val store = new Store()

    def main(arg: Array[String]): Unit = {
        val minesweeper = new Minesweeper(16, 16, 30)
        val neuralNets = store.readNeuralNetFromFile(NEURAL_NET, INITIAL_NETWORK_FACTORIES)

        val playWindow = new PlayWindow(minesweeper.cells)
        playWindow.observeGame(minesweeper)
        playWindow.run()

        val inputExtractor = new InputExtractor(minesweeper)
        while (true) {
            val explorables = inputExtractor.extractExplorablesWithExposedNeighbours()
            val cmd =
            if (explorables.isEmpty) {
                ExploreCommand(inputExtractor.random())
            } else {
                val coordinate = explorables
                    .maxBy(c => {
                        val area = inputExtractor.extractArea(c)
                        val input = InputExtractor.mapToInput(area)
                        if(ignoreFirstNetwork)
                            Math.abs(neuralNets(1).evaluate(input))
                        else
                            neuralNets(0).evaluate(input)
                    })

                val area = inputExtractor.extractArea(coordinate)
                val input = InputExtractor.mapToInput(area)
                val action = neuralNets(1).evaluate(input)
                if (action > 0) {
                    FlagCommand(coordinate)
                } else {
                    ExploreCommand(coordinate)
                }
            }
            Thread.sleep(500)
            playWindow.markAsNextMove(cmd.coordinate)
            Thread.sleep(500)
            minesweeper.execute(cmd)
        }
    }
}
