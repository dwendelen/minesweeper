package minesweeper.game

import javax.swing.JButton

class Cell() {
    var exposed = false
    var flag = false
    var number: Int = 0

    def bomb: Boolean = number == Cell.BOMB
}

object Cell {
    val BOMB = -1
}