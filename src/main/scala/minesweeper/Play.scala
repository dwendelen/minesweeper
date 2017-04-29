package minesweeper

import java.awt.GridLayout
import javax.swing.{JFrame, JOptionPane}

import minesweeper.game.{LostEvent, Minesweeper, WonEvent}
import minesweeper.ui.MineField

object Play {
    def main(arg: Array[String]): Unit = {
        val minesweeper = new Minesweeper(16, 16, 30)
        val bombField = new MineField(minesweeper)
        bombField.controlGame()
        bombField.observeGame()


        val frame = new JFrame("Minesweeper")
        minesweeper.observable().subscribe(_ match {
            case _: WonEvent =>
                JOptionPane.showMessageDialog(frame, "Won")
                minesweeper.reset()
            case _: LostEvent =>
                JOptionPane.showMessageDialog(frame, "Lost")
                minesweeper.reset()
            case _ =>
        })
        bombField.addToContainer(frame)
        frame.pack()
        frame.setVisible(true)
    }
}
