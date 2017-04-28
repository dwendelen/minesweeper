package minesweeper.neural

import minesweeper.store.{LayerDTO, NetworkDTO}

class NeuralNetwork(val inputs: List[Input],
                    val neurons: List[List[Neuron]]) {

    def setInput(values: List[Double]): Unit = {
        inputs
                .zip(values)
                .foreach { case (input, value) => input.value = value }
    }

    def evaluate(): List[Double] = {
        forward()
        neurons.last.map(_.value)
    }

    def learn(stepFactor: Double): Unit = {
        resetGradient()
        backwards()
        step(stepFactor)
    }

    private def resetGradient(): Unit = {
        neurons
                .flatten
                .foreach(_.resetGradient())
    }

    private def forward(): Unit = {
        neurons
                .flatten
                .foreach(_.forward())
    }

    private def backwards(): Unit = {
        neurons
                .flatten
                .reverse
                .foreach(_.backwards())
    }

    private def step(stepFactor: Double): Unit = {
        neurons
                .flatten
                .foreach(_.step(stepFactor))
    }


    def store(): NetworkDTO = {
        val neuronIdMap: Map[NeuronInput, Int] =
            (inputs ++ neurons.flatten)
                    .zipWithIndex
                    .toMap
        val layers = neurons.map(storeLayer(_, neuronIdMap))
        NetworkDTO(layers)
    }

    private def storeLayer(neurons: List[Neuron], neuronIdMap: Map[NeuronInput, Int]): LayerDTO = {
        val neuronDTOs = neurons.map(_.store(neuronIdMap))
        LayerDTO(neuronDTOs)
    }
}

object NeuralNetwork {
    def createRandom(nbOfInputs: Int, nbOfNeurons: List[Int]): NeuralNetwork = {
        val inputs = createInputs(nbOfInputs)
        val neurons = createNeurons(inputs, nbOfNeurons)

        neurons.last.foreach(_.gradient = 1)
        new NeuralNetwork(inputs, neurons)
    }

    private def createInputs(nbOfInputs: Int) = {
        (0 until nbOfInputs)
            .map(_ => new Input)
            .toList
    }

    private def createNeurons(inputs: List[Input], nbOfNeurons: List[Int]): List[List[Neuron]] = {
        val firstLayer: List[Neuron] = createLayer(inputs, nbOfNeurons.head)
        nbOfNeurons.tail
            .scanLeft(firstLayer)((previousLayer, nbOfNeurons) =>
                createLayer(previousLayer, nbOfNeurons)
            )
    }

    private def createLayer(previousLayer: List[NeuronInput], size: Int): List[Neuron] = {
        (0 until size)
            .map(_ => createNeuron(previousLayer))
            .toList
    }

    private def createNeuron(previousLayer: List[NeuronInput]) = {
        val neuron = new Neuron(createInputPairs(previousLayer), new Weight)
        neuron.randomiseWeights()
        neuron
    }

    private def createInputPairs(inputs: List[NeuronInput]): List[InputPair] = {
        inputs.map(i => InputPair(i, new Weight))
    }
}