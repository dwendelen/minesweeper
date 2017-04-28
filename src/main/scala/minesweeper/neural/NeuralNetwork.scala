package minesweeper.neural

import minesweeper.store.{LayerDTO, NetworkDTO, NeuronDTO}

class NeuralNetwork(val inputs: List[Input],
                    val hiddenLayers: List[List[Neuron]],
                    val output: Neuron) {

    //    def this() = {
    //        this(createInputs(nbOfInputs), createHiddenLayers(), createOutputNeuron())
    //    }

    def allNeuronLayers: List[List[Neuron]] = hiddenLayers ++ List(List(output))

    val weights = allNeuronLayers
            .flatten
            .flatMap(neuron => {
                val weightsOfInputs = neuron.inputs.map(_.weight)
                neuron.fixedValue :: weightsOfInputs
            })

    def setInput(index: Int, input: Double): Unit = {
        inputs(index).value = input
    }

    def evaluate(): Double = {
        forward()
        output.value
    }

    def learn(stepFactor: Double): Unit = {
        resetGradient()
        backwards()
        step(stepFactor)
    }

    private def resetGradient(): Unit = {
        (weights ++ inputs ++ hiddenLayers.flatten)
                .foreach(_.gradient = 0)
    }

    private def forward(): Unit = {
        allNeuronLayers
                .foreach(
                    layer => layer.foreach(
                        neuron => neuron.forward()
                    )
                )
    }

    private def backwards(): Unit = {
        allNeuronLayers
                .reverse
                .foreach(
                    layer => layer.foreach(
                        neuron => neuron.backwards()
                    )
                )
    }

    def step(stepFactor: Double): Unit = {
        weights.foreach(_.step(stepFactor))
    }


    def store(): NetworkDTO = {
        val neuronIdMap: Map[NeuronInput, Int] =
            (inputs ++ allNeuronLayers.flatten)
                    .zipWithIndex
                    .toMap
        val layers = allNeuronLayers.map(storeLayer(_, neuronIdMap))
        NetworkDTO(layers)
    }

    private def storeLayer(neurons: List[Neuron], neuronIdMap: Map[NeuronInput, Int]): LayerDTO = {
        val neuronDTOs = neurons.map(_.store(neuronIdMap))
        LayerDTO(neuronDTOs)
    }
}

object NeuralNetwork {
    def createRandom(nbOfInputs: Int, nbOfHiddenNeurons: List[Int]): NeuralNetwork = {
        val inputs = createInputs(nbOfInputs)
        val hiddenLayers = createHiddenLayers(inputs, nbOfHiddenNeurons)
        val outputNeuron = createOutputNeuron(hiddenLayers)

        new NeuralNetwork(inputs, hiddenLayers, outputNeuron)
    }

    private def createInputs(nbOfInputs: Int) = {
        (0 until nbOfInputs)
                .map(_ => new Input)
                .toList
    }

    private def createHiddenLayers(inputs: List[Input], nbOfHiddenNeurons: List[Int]): List[List[Neuron]] = {
        val firstLayer: List[Neuron] = createLayer(inputs, nbOfHiddenNeurons.head)
        nbOfHiddenNeurons.tail
                .scanLeft(firstLayer)(
                    (previousLayer, nbOfNeurons) => createLayer(previousLayer, nbOfNeurons)
                )
    }

    private def createLayer(previousLayer: List[NeuronInput], size: Int): List[Neuron] = {
        (0 until size)
                .map(_ => createNeuron(previousLayer))
                .toList
    }

    private def createNeuron(previousLayer: List[NeuronInput]) = {
        new Neuron(createInputPairs(previousLayer), new Weight)
    }

    private def createInputPairs(inputs: List[NeuronInput]): List[InputPair] = {
        inputs.map(i => InputPair(i, new Weight))
    }

    private def createOutputNeuron(hiddenLayers: List[List[Neuron]]): Neuron = {
        val outputNeuron = createNeuron(hiddenLayers.last)
        outputNeuron.gradient = 1
        outputNeuron
    }

    def fromDTO(networkDTO: NetworkDTO): NeuralNetwork = {
        def firstNeuron = networkDTO.layers(0).neurons(0)

        val nbOfInputs = firstNeuron.weights.size

        val inputs = (0 until nbOfInputs)
                .map(_ => new Input)
                .toList

        throw new UnsupportedOperationException
    }
}