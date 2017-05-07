package minesweeper.neural

import minesweeper.store.Store
import org.assertj.core.api.Assertions
import org.assertj.core.data.Percentage
import org.junit.Test

class NeuralNetworkTest {
    @Test
    def testGradients22(): Unit = {
        val network = NeuralNetwork.createRandom(2, List(2, 2))
        //network.setInput(0, 1d)
        //network.setInput(1, 2d)

        testNetwork(network)

        //val networkDTO = network.store()

    }

    @Test
    def testGradients11(): Unit = {
        val network = NeuralNetwork.createRandom(1, List(1))
        //network.setInput(0, 1d)

        testNetwork(network)
    }

    @Test
    def testGradients21(): Unit = {
        val network = NeuralNetwork.createRandom(1, List(1, 1))
        //network.setInput(0, 1d)

        testNetwork(network)
    }

    @Test
    def simplesNetwork() : Unit = {
        val network = NeuralNetwork.createRandom(1, List(1))
        for (i <- 0 until 20) {
            val value = network.evaluate(List(1))
            println(value)
            network.learn(1, 1)
        }
    }

    @Test
    def trainToOne(): Unit = {
        val network = NeuralNetwork.createRandom(1, List(3,3,3, 1))
        //network.setInput(0, )
        for (i <- 0 until 20) {
            val value = network.evaluate(List(1))
            println(value)
            network.learn(0.1, 1)
        }
    }


    @Test
    def trainToSin(): Unit = {
        val network = NeuralNetwork.createRandom(1, List(5, 1))

        val n = 10000
        val function = (x: Double) => Math.sin(x)
        val randomX = () => (Math.random() * 6) - 3

        for (i <- 0 until n) {
            val x = randomX()
            val sinX = function(x)

            val value = network.evaluate(List(x))
            val difference = sinX - value
            network.learn(0.1, sinX)
        }
        val store = new Store()
        store.writeToFile(List(network),"/tmp/sinStoreTest")
        val loadedNet = store.readNeuralNetFromFile("/tmp/sinStoreTest", () => null)(0)
        for (i <- 0 until 100) {
            val x = randomX()
            val sinX = function(x)

            //loadedNet.setInput(0, x)
            val value = loadedNet.evaluate(List(x))
            print(x.toString.replace('.', ','))
            print('\t')
            print(sinX.toString.replace('.', ','))
            print('\t')
            println(value.toString.replace('.', ','))
        }
    }


    def testNetwork(network: NeuralNetwork) = {
//        (network.inputs ++ network.weights)
//                .foreach(
//                    testGradient(network, _)
//                )
    }


    //def testGradient(network: NeuralNetwork, neuronInput: NeuronInput): Unit = {
//        val DELTA = 0.0001
//
//        val initialValue = network.evaluate()
//
//        network.learn(0)
//        val gradient = neuronInput.gradient
//
//        neuronInput.value += DELTA
//        val newValue = network.evaluate()
//
//        val actualDifference =0// newValue - initialValue
//        val expectedDifference = gradient * DELTA
//
//        println(expectedDifference)
//        println(actualDifference)

        //Assertions.assertThat(actualDifference).isCloseTo(expectedDifference, Percentage.withPercentage(0.1))
    //}
}
