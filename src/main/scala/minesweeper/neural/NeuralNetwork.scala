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
            new Layer(new DoubleMatrix(data), new DoubleMatrix(fixed))
        })
    }

    def evaluate(input: List[Double]): Double = {
        val inputArray = input.toArray[Double]
        val inputVector = new DoubleMatrix(inputArray)

        val output = layers.foldLeft(inputVector)((I, layer) => layer.forward(I))
        output.data.head
    }

    def learn(stepFactor: Double, expectedValue: Double): Double = {
        val outputLayer = layers.last
        val output = outputLayer.lastOutput.data(0)

        val error = expectedValue - output
        val step =
            if(expectedValue == 1 && output < 0) stepFactor
            else if (expectedValue == -1 && output > 0) -stepFactor
            else 0
        //val step = /*error * */stepFactor
        if(step != 0) {
            layers.foldRight(new DoubleMatrix(Array(1d)))((layer, grad) => layer.learn(step, grad))
        }
        error
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