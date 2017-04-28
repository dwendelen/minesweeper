package minesweeper.ui

import java.awt.event.ActionEvent
import java.awt.{Dimension, GridLayout}
import javax.swing.{JButton, JFrame}

import minesweeper.game.Minesweeper
import rx.lang.scala.Observable

object UI {
    def main(args: Array[String]): Unit = {
        new UI(new Minesweeper(16, 16, 40)).init()
        .subscribe()
        while(true) {

        }
    }
}

class UI(minesweeper: Minesweeper) {

    def init(): Observable[ClickEvent] = {
        minesweeper.subscribe()
                .subscribe(e => {
                    e.cell.button.setEnabled(false)
                    e.cell.button.setText(e.cell.number.toString)
                })
        val frame = new JFrame("FrameDemo")

        Observable.apply[ClickEvent](subscriber => {
            //2. Optional: What happens when the frame closes?
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

            val grid = new GridLayout(minesweeper.width, minesweeper.height)
            frame.setLayout(grid)
            (0 until minesweeper.height).foreach(
                y => (0 until minesweeper.width).foreach(
                    x => {
                        val button = new JButton("")
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
                .doOnNext(click => {
                    minesweeper.click(click.x, click.y)
                })

    }
}

case class ClickEvent(x: Int, y: Int, button: JButton)
