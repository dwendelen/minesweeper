package minesweeper

import minesweeper.neural.NeuralNetwork

object Config {
    val STEP_FACTOR: Double = 0.1
    val DATA_SET: String = "/Users/daanw/neural/dataset.json"
    val NEURAL_NET_1: String = "/Users/daanw/neural/neuralnet_100_100_1.json"
    val NEURAL_NET_2: String = "/Users/daanw/neural/neuralnet_100_100_2.json"
    val INITIAL_NETWORK_FACTORY: () => NeuralNetwork = () => NeuralNetwork.createRandom(100, List(100, 100, 1))
}
