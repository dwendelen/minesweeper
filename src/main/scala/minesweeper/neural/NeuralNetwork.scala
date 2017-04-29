package minesweeper.neural

import org.jblas.DoubleMatrix

import scala.collection.JavaConverters._

class NeuralNetwork(weights: List[List[List[Double]]]) {
    private val layers: List[Layer] = mapToLayers

    private def mapToLayers = {
        weights.map(neurons => {
            val data = neurons.map(row =>
                row.slice(0, row.size - 1).toArray[Double])
                    .toArray[Array[Double]]
            val fixed = neurons.map(_.last).toArray[Double]
            new Layer(null, new DoubleMatrix(data), new DoubleMatrix(fixed), null)
        })
    }

    def evaluate(input: List[Double]): List[Double] = {
        val inputArray = input.toArray[Double]
        val inputVector = new DoubleMatrix(inputArray)

        val output = layers.foldLeft(inputVector)((I, layer) => layer.forward(I))
        output.data.toList
    }

    def learn(stepFactor: Double, expectedOutputs: List[Double]): List[Double] = {
        val outputLayer = layers.last
        val outputs = outputLayer.lastOutput

        expectedOutputs
                .zipWithIndex
                .map { case (expectedValue, row) =>
                    val error = expectedValue - outputs.data(row)
                    val step = error * stepFactor

                    val gradientLastHiddenLayer = outputLayer.learnLastLayer(step, row)
                    layers.slice(0, layers.size - 1)
                            .foldRight(gradientLastHiddenLayer)((layer, grad) => layer.learn(step, grad))
                    error
                }
    }


    def store(): List[List[List[Double]]] = {
        layers.map(layer => {
            val neurons = layer.weights.rowsAsList().asScala
            val rows = neurons.map(neuron => neuron.data.toList).toList

            rows
                    .zip(layer.fixedWeights.data.toList)
                    .map { case (row, fixed) => row ++ List(fixed) }
        })
    }
}

object NeuralNetwork {
    def createRandom(nbOfInputs: Int, nbOfNeurons: List[Int]): NeuralNetwork = {
        new NeuralNetwork(createLayers(nbOfInputs, nbOfNeurons))
    }

    private def createLayers(nbOfInputs: Int, nbOfNeurons: List[Int]): List[List[List[Double]]] = {
        val dummyLayer: List[List[Double]] = List.tabulate(nbOfInputs)(_ => List())
        nbOfNeurons
                .foldLeft(List(dummyLayer))((previousLayers, nbOfNeurons) => {
                    val newLayer = createLayer(previousLayers.last.size + 1, nbOfNeurons)
                    previousLayers ++ List(newLayer)
                })
                .tail
    }

    private def createLayer(nbOfColumns: Int, nbOfRows: Int): List[List[Double]] = {
        (0 until nbOfRows)
                .map(_ => createNeuron(nbOfColumns))
                .toList
    }

    private def createNeuron(nbOfColumns: Int): List[Double] = {
        (0 until nbOfColumns)
                .map(_ => (2 * Math.random() - 1) / nbOfColumns)
                .toList
    }
}