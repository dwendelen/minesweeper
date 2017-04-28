package minesweeper.ui

import java.awt.event.ActionEvent
import java.awt.{Color, Dimension, GridLayout}
import javax.swing.{JButton, JFrame, JOptionPane}

import minesweeper.game._
import rx.lang.scala.Observable

object UI {
    def main(args: Array[String]): Unit = {
        val minesweeper = new Minesweeper(16, 16, 30)
        new UI(minesweeper)
                .init()
                .subscribe(e => e match {
                    case click: ClickEvent =>
                        minesweeper.click(click.x, click.y)
                    case _ =>
                })
        while (true) {

        }
    }

}

class UI(minesweeper: Minesweeper) {
    def init(): Observable[UIEvent] = {
        val frame = new JFrame("FrameDemo")
        minesweeper.observable()
                .subscribe((ev: MinesweeperEvent) => ev match {
                    case e: OpenedEvent =>
                        val button = e.cell.button
                        button.setForeground(Color.LIGHT_GRAY)
                        button.setBackground(Color.LIGHT_GRAY)
                        button.setEnabled(false)
                        button.setText(e.cell.number.toString)
                    case e: WonEvent =>
                        JOptionPane.showMessageDialog(frame, "Won");
                    case e: LostEvent =>
                        JOptionPane.showMessageDialog(frame, "Lost");
                })


        Observable.apply[ClickEvent](subscriber => {
            //2. Optional: What happens when the frame closes?
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

            val grid = new GridLayout(minesweeper.width, minesweeper.height)
            frame.setLayout(grid)
            (0 until minesweeper.height).foreach(
                y => (0 until minesweeper.width).foreach(
                    x => {
                        val button = new JButton("")
                        button.setForeground(Color.BLUE)
                        button.setBackground(Color.BLUE)
                        button.setOpaque(true)
                        button.setPreferredSize(new Dimension(30, 30))
                        button.addActionListener((e: ActionEvent) => {
                            println(e)
                            subscriber.onNext(ClickEvent(x, y, button))
                        })
                        minesweeper.setButton(x, y, button)
                        frame.add(button)
                    }
                )
            )


            //4. Size the frame.
            frame.pack()
            frame.setVisible(true)
            //5. Show it.
        })


    }

}

abstract sealed class UIEvent()

case class ClickEvent(x: Int, y: Int, button: JButton) extends UIEvent

case class SendEvent() extends UIEvent
