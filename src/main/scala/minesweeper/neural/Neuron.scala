package minesweeper.neural

import minesweeper.store.NeuronDTO

class Neuron(val inputs: List[InputPair], val fixedValue: Weight) extends NeuronInput {
    def forward(): Unit = {
        applyActivation(sum())
    }

    private def sum(): Double = {
        inputs.map(pair => pair.weight.value * pair.neuron.value)
                .sum + fixedValue.value
    }

    private def applyActivation(sum: Double): Unit = {
        value = sigmoid(sum)
    }

    def backwards(): Unit = {
        val factor = dsigmoid(value) * gradient
        fixedValue.gradient += factor
        inputs.foreach(pair => {
            backward(pair.neuron, pair.weight, factor)
            backward(pair.weight, pair.neuron, factor)
        })
    }

    def backward(affected: NeuronInput, other: NeuronInput, factor: Double): Unit = {
        affected.gradient += other.value * factor
    }

    private def sigmoid(x: Double): Double = {
        1.0d / (1.0d + Math.exp(-x))
        (Math.exp(x) - Math.exp(-x)) / (Math.exp(x) + Math.exp(-x))
    }

    private def dsigmoid(sigmoidOfTheSum: Double): Double = {
        //sigmoidOfTheSum * (1 - sigmoidOfTheSum)
        val x = sum()
        1 - sigmoidOfTheSum * sigmoidOfTheSum

    }

    def store(neuronIdMap: Map[NeuronInput, Int]): NeuronDTO = {
        NeuronDTO(neuronIdMap(this), fixedValue.value, storeInputs(neuronIdMap))
    }

    def storeInputs(neuronIdMap: Map[NeuronInput, Int]): Map[String, Double] = {
        inputs.map(
            inputPair => (neuronIdMap(inputPair.neuron).toString, inputPair.weight.value)
        ).toMap
    }
}

case class InputPair(neuron: NeuronInput, weight: Weight)