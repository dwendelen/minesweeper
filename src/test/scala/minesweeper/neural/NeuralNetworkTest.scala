package minesweeper.neural

import minesweeper.store.Store
import org.assertj.core.api.Assertions
import org.assertj.core.data.Percentage
import org.junit.Test

class NeuralNetworkTest {
    @Test
    def testGradients22(): Unit = {
        val network = NeuralNetwork.createRandom(2, List(2, 2))
        network.setInput(0, 1d)
        network.setInput(1, 2d)

        testNetwork(network)

        val networkDTO = network.store()

    }

    @Test
    def testGradients11(): Unit = {
        val network = NeuralNetwork.createRandom(1, List(1))
        network.setInput(0, 1d)

        testNetwork(network)
    }

    @Test
    def testGradients21(): Unit = {
        val network = NeuralNetwork.createRandom(1, List(1, 1))
        network.setInput(0, 1d)

        testNetwork(network)
    }

    @Test
    def trainToZero(): Unit = {
        val network = NeuralNetwork.createRandom(1, List(3, 3, 3))
        network.setInput(0, Math.random())

        for (i <- 0 until 20) {
            val value = network.evaluate
            println(value)
            network.learn(10 * -value)
        }
    }


    @Test
    def trainToSin(): Unit = {
        val network = NeuralNetwork.createRandom(1, List(5))

        network.inputs.foreach(_.value = 0)
        val n = 100000
        val function = (x: Double) => Math.sin(x)
        val randomX = () => (Math.random() * 6) - 3

        for (i <- 0 until n) {
            val x = randomX()
            val sinX = function(x)

            network.setInput(0, x)
            val value = network.evaluate()
            val difference = sinX - value
            network.learn(0.1 * difference)
        }
        val store = new Store()
        store.writeToFile(network,"/tmp/sinStoreTest")
        val loadedNet = store.readNeuralNetFromFile("/tmp/sinStoreTest", () => null)
        for (i <- 0 until 100) {
            val x = randomX()
            val sinX = function(x)

            loadedNet.setInput(0, x)
            val value = loadedNet.evaluate()
            print(x.toString.replace('.', ','))
            print('\t')
            print(sinX.toString.replace('.', ','))
            print('\t')
            println(value.toString.replace('.', ','))
        }
    }


    def testNetwork(network: NeuralNetwork) = {
        (network.inputs ++ network.weights)
                .foreach(
                    testGradient(network, _)
                )
    }


    def testGradient(network: NeuralNetwork, neuronInput: NeuronInput): Unit = {
        val DELTA = 0.0001

        val initialValue = network.evaluate()

        network.learn(0)
        val gradient = neuronInput.gradient

        neuronInput.value += DELTA
        val newValue = network.evaluate()

        val actualDifference = newValue - initialValue
        val expectedDifference = gradient * DELTA

        println(expectedDifference)
        println(actualDifference)

        Assertions.assertThat(actualDifference).isCloseTo(expectedDifference, Percentage.withPercentage(0.1))
    }
}
