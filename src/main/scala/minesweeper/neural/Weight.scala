package minesweeper.neural

class Weight extends NeuronInput {
    value = 2*Math.random() - 1

    def step(factor: Double) = {
        value += factor * gradient
    }
}
