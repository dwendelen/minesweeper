package minesweeper.game

import javax.swing.JButton

class Cell() {
    var exposed = false
    var flag = false
    var button: JButton = _
    var number: Int = 0

    def clickable:Boolean = !(exposed || flag)
    def bomb: Boolean = number == Cell.BOMB
}

object Cell {
    val BOMB = -1
}