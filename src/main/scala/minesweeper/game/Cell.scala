package minesweeper.game

import javax.swing.JButton

import minesweeper.util.Coordinate

class Cell(val coordinate: Coordinate) {
    var exposed = false
    var flag = false
    var number: Int = 0

    def bomb: Boolean = number == Cell.BOMB
    def explorable: Boolean = !(exposed || flag)
}

object Cell {
    val BOMB = -1
}