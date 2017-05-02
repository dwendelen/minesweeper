package minesweeper

import java.awt.GridLayout
import javax.swing.{JFrame, JOptionPane}

import minesweeper.game.{LostEvent, Minesweeper, WonEvent}
import minesweeper.ui.{MineField, PlayWindow}

object Play {
    def main(arg: Array[String]): Unit = {
        val minesweeper = new Minesweeper(16, 16, 30)
        val playWindow = new PlayWindow(minesweeper.cells)
        playWindow.controlAndObserverGame(minesweeper)
        playWindow.run()
    }
}
