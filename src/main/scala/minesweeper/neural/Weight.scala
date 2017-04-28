package minesweeper.neural

class Weight(initialValue: Double) extends NeuronInput {
    value = initialValue

    def this() = this(2 * Math.random() - 1)

    def step(factor: Double): Unit = {
        value += factor * gradient
    }
}
