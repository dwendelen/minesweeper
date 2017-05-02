package minesweeper.util

object Coordinate {
    def tabulate(center: Coordinate, radius: Int): List[List[Coordinate]] = {
        val size = 2 * radius + 1
        val items = List.tabulate(size, size)((x, y) =>
            Coordinate(x - radius + center.x, y - radius + center.y)
        )
        //new Grid(items)
        items
    }
}

case class Coordinate(x: Int, y: Int)