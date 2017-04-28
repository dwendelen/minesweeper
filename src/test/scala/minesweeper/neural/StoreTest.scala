package minesweeper.store

import minesweeper.learn.{DataPoint, DataSet}
import minesweeper.neural.NeuralNetwork
import org.assertj.core.api.Assertions
import org.assertj.core.data.Percentage
import org.junit.Test

class StoreTest {
    val store = new Store()

    @Test
    def testGradients22(): Unit = {
        val network = NeuralNetwork.createRandom(2, List(2, 2))
        network.setInput(0, 1d)
        network.setInput(1, 2d)

        println( store writeToString network )

    }

    @Test
    def writeReadDataSet(): Unit = {
        val dataSet = DataSet(List(
            DataPoint(List(1, 2), 3),
            DataPoint(List(4, 6), 33),
            DataPoint(List(14, 21, 34), 32),
            DataPoint(List(123, 22), 3234)
        ))

        store.writeToFile(dataSet,"/tmp/storeTestDataSet")
        val actual = store.readDataSetFromFile("/tmp/storeTestDataSet", () => null)

        Assertions.assertThat(actual).isEqualTo(dataSet)
    }

    @Test
    def writeReadNeuralNet(): Unit = {
        val network = NeuralNetwork.createRandom(2, List(2, 2))
        network.setInput(0, 1d)
        network.setInput(1, 2d)

        network.weights.foreach(_.gradient = 0)
        network.output.gradient = 0
        network.inputs.foreach(_.value = 0)
        network.allNeuronLayers.flatten.foreach(_.value = 0)

        store.writeToFile(network,"/tmp/storeTestNetwork")
        val actual = store.readNeuralNetFromFile("/tmp/storeTestNetwork", () => null)


        val networkJson: String = store.writeToString(network)
        val actualJson: String = store.writeToString(actual)

        Assertions.assertThat(networkJson).isEqualTo(actualJson)
    }
}