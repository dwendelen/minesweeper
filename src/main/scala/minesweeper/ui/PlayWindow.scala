package minesweeper.ui

import javax.swing.{JFrame, JOptionPane}

import minesweeper.game.{Cell, LostEvent, Minesweeper, WonEvent}
import minesweeper.util.{Coordinate, Grid}

class PlayWindow(cells: Grid[Cell]) {
    private val bombField = new MineField(cells)
    private val frame = new JFrame("Minesweeper")

    def run(): Unit = {
        bombField.addToContainer(frame)
        frame.pack()
        frame.setVisible(true)
    }

    def controlAndObserverGame(minesweeper: Minesweeper): Unit = {
        minesweeper.observable().subscribe(_ match {
            case _: WonEvent =>
                JOptionPane.showMessageDialog(frame, "Won")
                minesweeper.reset()
            case _: LostEvent =>
                JOptionPane.showMessageDialog(frame, "Lost")
                minesweeper.reset()
            case _ =>
        })

        bombField.controlGame(minesweeper)
        observeGame(minesweeper)
    }

    def observeGame(minesweeper: Minesweeper): Unit = {
        bombField.observeGame(minesweeper)
    }

    def markAsNextMove(coordinate: Coordinate) : Unit = {
        bombField.markButton(coordinate, "...")
    }
}
