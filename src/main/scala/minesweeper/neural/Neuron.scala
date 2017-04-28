package minesweeper.neural

import minesweeper.store.NeuronDTO

class Neuron(val inputs: List[InputPair], val fixedValue: Weight) extends NeuronInput {
    def step(stepFactor: Double): Unit = {
        inputs.foreach(_.step(stepFactor))
    }

    def weights: List[Weight] = fixedValue :: inputs.map(_.weight)

    def forward(): Unit = {
        val sumInputs = inputs
                .map(_.value)
                .sum
        val sum = sumInputs + fixedValue.value
        tanh(sum)
    }

    def randomiseWeights(): Unit = {
        val size = weights.size
        weights.foreach(_.value = Math.random() / size)
    }

    def resetGradient(): Unit = {
        weights.foreach(_.gradient = 0)
    }

    def backwards(): Unit = {
        val factor = dtanh(value) * gradient
        fixedValue.gradient += factor
        inputs.foreach(_.backwards(factor))
    }

    private def tanh(x: Double): Double = {
        (Math.exp(x) - Math.exp(-x)) / (Math.exp(x) + Math.exp(-x))
    }

    private def dtanh(sigmoidOfTheSum: Double): Double = {
        1 - sigmoidOfTheSum * sigmoidOfTheSum
    }

    def store(neuronIdMap: Map[NeuronInput, Int]): NeuronDTO = {
        NeuronDTO(neuronIdMap(this), fixedValue.value, storeInputs(neuronIdMap))
    }

    def storeInputs(neuronIdMap: Map[NeuronInput, Int]): Map[String, Double] = {
        inputs
            .map(_.store(neuronIdMap))
            .toMap
    }
}

