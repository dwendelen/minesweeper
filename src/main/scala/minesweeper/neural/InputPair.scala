package minesweeper.neural

case class InputPair(neuron: NeuronInput, weight: Weight) {
    def value: Double = neuron.value * weight.value

    def backwards(factor: Double): Unit = {
        weight.gradient += neuron.value * factor
        neuron.gradient += weight.value * factor
    }

    def step(stepFactor: Double): Unit = {
        weight.value += weight.gradient * stepFactor
    }

    def store(neuronIdMap: Map[NeuronInput, Int]): (String, Double) = {
        (neuronIdMap(neuron).toString, weight.value)
    }
}
