package minesweeper.util

object Grid {
    def tabulate[A](width: Int, height: Int)(f: (Int, Int) => A): Grid[A] = {
        val items = List.tabulate(width, height)(f)
        Grid(items)
    }
}

case class Grid[A](items: List[List[A]]) extends Traversable[A] {
    def width :Int= items.size

    def height :Int= items.head.size

    override def foreach[U](f: (A) => U): Unit = {
        items.flatten.foreach(f)
    }

    def map[B](f: A => B): Grid[B] = {
        val mappedItems = items.map(_.map(f))
        Grid(mappedItems)
    }

    def zip[B](other: Grid[B]): Grid[(A, B)] = {
        val zippedItems = items.zip(other.items).map(p => p._1 zip p._2)
        Grid(zippedItems)
    }

    def apply(x: Int, y: Int): A = items(x)(y)

    def apply(coordinate: Coordinate): A = apply(coordinate.x, coordinate.y)

    override def toList(): List[A] = items.flatten
}