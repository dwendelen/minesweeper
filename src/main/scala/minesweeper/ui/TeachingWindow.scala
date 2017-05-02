package minesweeper.ui

import java.awt.event.{MouseAdapter, MouseEvent}
import javax.swing.{BoxLayout, JButton, JFrame}

import minesweeper.game._
import minesweeper.teach._
import minesweeper.util.Grid
import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject

class TeachingWindow() {
    private val mineField = new MineField(5, 5)
    private val subject = PublishSubject[TeachingCommand]()

    def observeCommands(): Observable[TeachingCommand] = subject

    def run(): Unit = {
        val frame = new JFrame("Learn")
        frame.setLayout(new BoxLayout(frame.getContentPane, BoxLayout.Y_AXIS))
        mineField.addToContainer(frame)

        frame.add(createButton("Skip", Skip()))
        frame.add(createButton("Explore", Explore()))
        frame.add(createButton("Flag", Flag()))

        frame.pack()
        frame.setVisible(true)
    }

    def createButton(name: String, teachingCommand: TeachingCommand): JButton = {
        val button = new JButton(name)
        button.addMouseListener(new MouseAdapter {
            override def mouseClicked(var1: MouseEvent): Unit = {
                subject.onNext(teachingCommand)
            }
        })
        button
    }

    def display(scenario: Grid[Cell]): Unit = {
        mineField.repopulate(scenario)
        mineField.markButton(TeachingSession.CENTER, "?")
    }

    def controlAndObserverTeachingSession(teachingSession: TeachingSession): Unit = {
        teachingSession.observeScenarios()
                .subscribe(scenario => display(scenario))
        observeCommands()
                .subscribe(cmd => teachingSession.execute(cmd))
    }
}

