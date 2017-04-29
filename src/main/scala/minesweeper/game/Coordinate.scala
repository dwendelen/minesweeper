package minesweeper.game

object Coordinate {
    def tabulate(center: Coordinate, radius: Int): List[List[Coordinate]] = {
        val size = 2 * radius + 1
        List.tabulate(size, size)((x, y) =>
            Coordinate(x - radius + center.x, y - radius + center.y)
        )
    }
}

case class Coordinate(x: Int, y: Int)