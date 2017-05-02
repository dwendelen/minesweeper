package minesweeper.teach

import minesweeper.game.{Cell, ExploreCommand, Minesweeper, MinesweeperCommand}
import minesweeper.learn.{DataPoint, InputExtractor}
import minesweeper.util.{Coordinate, Grid}
import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject

import scala.util.Random

object TeachingSession {
    val CENTER = Coordinate(2, 2)
}

class TeachingSession(minesweeper: Minesweeper) {
    type Scenario = Grid[Cell]
    private val inputExtractor = new InputExtractor(minesweeper)

    private var todo: List[Scenario] = _
    private var recordedCommands: List[MinesweeperCommand] = List()

    private var currentScenario: Scenario = _
    private var scenarios = PublishSubject[Scenario]()
    private var dataPoints = PublishSubject[DataPoint]()

    def observeScenarios(): Observable[Grid[Cell]] = scenarios

    def observeNewDataPoints(): Observable[DataPoint] = dataPoints

    def execute(c: TeachingCommand): Unit = {
        val input = InputExtractor.mapToInput(currentScenario)
        dataPoints.onNext(DataPoint(input, c.output))

        if (!c.isInstanceOf[Skip]) {
            val coordinate = currentScenario(TeachingSession.CENTER).coordinate
            recordedCommands = c.createCommand(coordinate) :: recordedCommands
        }
        doNextScenario()
    }

    def start(): Unit = {
        doANewGame()
    }

    private def doANewGame(): Unit = {
        minesweeper.reset()
        doNextFrame()
    }

    private def doNextFrame(): Unit = {
        recordedCommands.foreach(minesweeper.execute)

        if (recordedCommands.isEmpty) {
            val randomCoordinate = inputExtractor.random()
            minesweeper.execute(ExploreCommand(randomCoordinate))
        }

        val scenarios = inputExtractor.extractExplorables()
                .map(inputExtractor.extractArea)
                .map(new Grid(_))
        todo = Random.shuffle(scenarios)
        doNextScenario()
    }

    private def doNextScenario(): Unit = todo match {
        case Nil =>
            doNextFrame()
        case newScenario :: otherScenarios =>
            todo = otherScenarios
            currentScenario = newScenario

            if (noneOfTheNeighboursAreExposed(newScenario)) {
                doNextScenario()
            } else {
                scenarios.onNext(currentScenario)
            }
    }

    private def noneOfTheNeighboursAreExposed(newScenario: Scenario) = {
        val neighbours = Coordinate.tabulate(TeachingSession.CENTER, 1).flatten
        newScenario
                .filter(cell => cell != null)
                .filter(cell => neighbours.contains(cell.coordinate))
                .forall(cell => !cell.exposed)
    }
}