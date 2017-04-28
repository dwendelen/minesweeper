package minesweeper.neural

class NeuralNetwork(nbOfInputs: Int, nbOfHiddenNeurons: List[Int]) {
    val inputs: List[Input] = createInputs()
    val hiddenLayers: List[List[Neuron]] = createHiddenLayers()
    val output: Neuron = createOutputNeuron()

    def allNeuronLayers: List[List[Neuron]] = hiddenLayers ++ List(List(output))

    val weights = allNeuronLayers
            .flatten
            .flatMap(neuron => {
                val weightsOfInputs = neuron.inputs.map(_.weight)
                neuron.fixedValue :: weightsOfInputs
            })


    private def createInputs() = {
        (0 until nbOfInputs)
                .map(_ => new Input)
                .toList
    }

    private def createHiddenLayers(): List[List[Neuron]] = {
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

    private def createOutputNeuron(): Neuron = {
        val outputNeuron = createNeuron(hiddenLayers.last)
        outputNeuron.gradient = 1
        outputNeuron
    }

    def setInput(index: Int, input: Double): Unit = {
        inputs(index).value = input
    }

    def getWeights: List[Double] = {
        weights.map(_.value)
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
}