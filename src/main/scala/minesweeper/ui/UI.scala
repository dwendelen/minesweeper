package minesweeper.ui

import java.awt.event.ActionEvent
import java.awt.{Color, Dimension, GridLayout}
import javax.swing.{JButton, JFrame, JOptionPane}

import minesweeper.game._
import rx.lang.scala.Observable

object UI {
    def main(args: Array[String]): Unit = {
        val minesweeper = new Minesweeper(16, 16, 30)
        new UI(minesweeper, false)
                .init()
                .subscribe(e => e match {
                    case click: ClickEvent =>
                        //minesweeper.explore(click.x, click.y)
                    case _ =>
                })
        while (true) {

        }
    }

}

class UI(minesweeper: Minesweeper, learn: Boolean, play: Boolean = false) {
    def init(): Observable[UIEvent] = {
        val frame = new JFrame("FrameDemo")
        minesweeper.observable()
//                .subscribe((ev: MinesweeperEvent) => ev match {
//                    case e: ExposedEvent =>
//                        val button = e.cell.button
//                        button.setForeground(Color.LIGHT_GRAY)
//                        button.setBackground(Color.LIGHT_GRAY)
//                        button.setEnabled(false)
//                        button.setText(e.cell.number.toString)
//                    case e: FlaggedEvent =>
//                        val button = e.cell.button
//                        button.setText("F")
//                    case e: WonEvent =>
//                        JOptionPane.showMessageDialog(frame, "Won");
//                    case e: LostEvent =>
//                        JOptionPane.showMessageDialog(frame, "Lost");
//                })


        Observable.apply[UIEvent](subscriber => {
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
                            subscriber.onNext(ClickEvent(x, y, button))
                        })
//                        minesweeper.setButton(x, y, button)
                        frame.add(button)
                    }
                )
            )


            //4. Size the frame.
            frame.pack()
            frame.setVisible(true)
            //5. Show it.

            if (learn) {
                val frame2 = new JFrame("Stuff")
                val grid2 = new GridLayout(1, 2)
                frame2.setLayout(grid2)
                val sendButton = new JButton("Send")
                sendButton.addActionListener((e: ActionEvent) => {
                    subscriber.onNext(SendEvent())
                })
                frame2.add(sendButton)
                val randomButton = new JButton("Random")
                randomButton.addActionListener((e: ActionEvent) => {
                    subscriber.onNext(RandomEvent())
                })
                frame2.add(randomButton)
                frame2.pack()
                frame2.setVisible(true)
            }

            if(play) {
                val frame3 = new JFrame("Play")
                val grid3 = new GridLayout(1, 2)
                frame3.setLayout(grid3)
                val moveButton = new JButton("Move")
                moveButton.addActionListener((e: ActionEvent) => {
                    subscriber.onNext(MoveEvent())
                })
                frame3.add(moveButton)
                frame3.pack()
                frame3.setVisible(true)
            }
        })


    }

}

abstract sealed class UIEvent()

case class ClickEvent(x: Int, y: Int, button: JButton) extends UIEvent

case class SendEvent() extends UIEvent

case class RandomEvent() extends UIEvent
case class MoveEvent() extends UIEvent
