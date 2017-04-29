package minesweeper.ui

import java.awt.event.{MouseAdapter, MouseEvent}
import java.awt.{Color, Container, Dimension, GridLayout}
import javax.swing.JButton

import minesweeper.game._
import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject

class MineField(minesweeper: Minesweeper) {
    private val subject = PublishSubject[MinesweeperCommand]()
    private val container = new Container

    private val buttons: List[List[JButton]] =
        List.tabulate(minesweeper.width, minesweeper.height)(createButton)

    container.setLayout(new GridLayout(minesweeper.height, minesweeper.width))
    buttons.flatten.foreach(container.add)

    def createButton(x: Int, y: Int): JButton = {
        val coordinate = Coordinate(x, y)
        val button = new JButton()
        button.setOpaque(true)
        button.setPreferredSize(new Dimension(30, 30))
        resetButton(button)

        button.addMouseListener(new MouseAdapter {
            override def mouseClicked(e: MouseEvent): Unit = {
                e.getButton match {
                    case MouseEvent.BUTTON1 =>
                        if (button.isEnabled) {
                            subject.onNext(ExploreCommand(coordinate))
                        } else {
                            subject.onNext(ExploreNeighboursCommand(coordinate))
                        }
                    case MouseEvent.BUTTON3 =>
                        if(button.getText == "") {
                            subject.onNext(FlagCommand(coordinate))
                        } else {
                            subject.onNext(UnflagCommand(coordinate))
                        }
                    case _ =>
                }
            }
        })
        button
    }

    def addToContainer(containerToAddThisTo: Container): Unit = {
        containerToAddThisTo.add(container)
    }

    def observable(): Observable[MinesweeperCommand] = subject

    def controlGame(): Unit = {
        subject.subscribe(c => minesweeper.execute(c))
    }

    def observeGame(): Unit = {
        minesweeper.observable()
                .subscribe(_ match {
                    case e: ExposedEvent =>
                        expose(e.coordinate, e.number)
                    case e: FlaggedEvent =>
                        flag(e.coordinate)
                    case e: UnflaggedEvent =>
                        unflag(e.coordinate)
                    case _: GameResetEvent =>
                        reset()
                    case _ =>
                })
    }

    def expose(coordinate: Coordinate, number: Int): Unit = {
        val button = get(coordinate)
        button.setForeground(Color.LIGHT_GRAY)
        button.setBackground(Color.LIGHT_GRAY)
        button.setEnabled(false)
        button.setText(textForNumber(number))
    }

    private def textForNumber(number: Int) = number match {
        case Cell.BOMB => "X"
        case 0 => ""
        case _ => number.toString
    }

    def flag(coordinate: Coordinate): Unit = {
        val button = get(coordinate)
        button.setText("F")
    }

    def unflag(coordinate: Coordinate): Unit = {
        val button = get(coordinate)
        button.setText("")
    }

    def reset(): Unit = {
        buttons.flatten.foreach(resetButton)
    }

    private def resetButton(b: JButton) = {
        b.setForeground(Color.BLUE)
        b.setBackground(Color.BLUE)
        b.setEnabled(true)
        b.setText("")
    }

    private def get(coordinate: Coordinate): JButton = {
        buttons(coordinate.x)(coordinate.y)
    }
}