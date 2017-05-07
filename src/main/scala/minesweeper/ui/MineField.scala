package minesweeper.ui

import java.awt.event.{MouseAdapter, MouseEvent}
import java.awt._
import javax.swing.JButton

import minesweeper.game._
import minesweeper.util.{Coordinate, Grid}
import rx.lang.scala.Observable
import rx.lang.scala.subjects.PublishSubject

class MineField(width: Int, height: Int) {
    def this(cells: Grid[Cell]) = {
        this(cells.width, cells.height)
        repopulate(cells)
    }

    private val subject = PublishSubject[MinesweeperCommand]()
    private val container = new Container

    private val buttons: Grid[Button] =
        Grid.tabulate(width, height)((_, _) => createButton())

    container.setLayout(new GridLayout(height, width))
    buttons.foreach(button => container.add(button.jButton))

    private def createButton(): Button = {
        val jbutton = new JButton()
        jbutton.setMargin(new Insets(0, 0, 0, 0))

        val button = Button(jbutton, null)

        jbutton.setOpaque(true)
        jbutton.setPreferredSize(new Dimension(30, 30))
        jbutton.setForeground(Color.BLUE)
        //jbutton.setBackground(Color.BLUE)
        jbutton.setEnabled(true)

        jbutton.addMouseListener(new MouseAdapter {
            override def mouseClicked(e: MouseEvent): Unit = {
                e.getButton match {
                    case MouseEvent.BUTTON1 =>
                        if (jbutton.isEnabled) {
                            subject.onNext(ExploreCommand(button.coordinate))
                        } else {
                            subject.onNext(ExploreNeighboursCommand(button.coordinate))
                        }
                    case MouseEvent.BUTTON3 =>
                        if (jbutton.getText == "") {
                            subject.onNext(FlagCommand(button.coordinate))
                        } else {
                            subject.onNext(UnflagCommand(button.coordinate))
                        }
                    case _ =>
                }
            }
        })
        button
    }

    def repopulate(cells: Grid[Cell]): Unit = {
        buttons.zip(cells).foreach { case (button, cell) =>
            button.coordinate = if (cell == null) null else cell.coordinate
            updateJButton(button.jButton, cell)
        }
    }

    def addToContainer(containerToAddThisTo: Container): Unit = {
        containerToAddThisTo.add(container)
    }

    def observable(): Observable[MinesweeperCommand] = subject

    def controlGame(minesweeper: Minesweeper): Unit = {
        subject.subscribe(c => minesweeper.execute(c))
    }

    def observeGame(minesweeper: Minesweeper): Unit = {
        minesweeper.observable()
                .subscribe(_ match {
                    case e: ExposedEvent =>
                        updateJButton(e.cell)
                    case e: FlaggedEvent =>
                        updateJButton(e.cell)
                    case e: UnflaggedEvent =>
                        updateJButton(e.cell)
                    case _: GameResetEvent =>
                        repopulate(minesweeper.cells)
                    case _ =>
                })
    }

    def updateJButton(cell: Cell): Unit = {
        val button = get(cell.coordinate)
        if (button == null) {
            return
        }

        updateJButton(button, cell)
    }

    def markButton(coordinateIn: Coordinate, text:String): Unit = {
        buttons(coordinateIn).jButton.setText(text)
    }

    private def updateJButton(button: JButton, cell: Cell): Unit = {
        if (cell == null) {
            button.setVisible(false)
            return
        }

        button.setVisible(true)
        button.setText(textForCell(cell))

        if (cell.exposed) {
            button.setForeground(Color.LIGHT_GRAY)
            //button.setBackground(Color.LIGHT_GRAY)
            button.setEnabled(false)
        } else {
            button.setForeground(Color.BLUE)
            //button.setBackground(Color.BLUE)
            button.setEnabled(true)
        }
    }

    private def textForCell(cell: Cell): String = {
        if (cell.exposed) {
            cell.number match {
                case Cell.BOMB => "X"
                case 0 => ""
                case _ => cell.number.toString
            }
        } else if (cell.flag) {
            "F"
        } else {
            ""
        }
    }

    private def get(coordinate: Coordinate): JButton = {
        buttons.find(_.coordinate == coordinate)
                .map(_.jButton)
                .orNull
    }

    private case class Button(jButton: JButton, var coordinate: Coordinate)
}

