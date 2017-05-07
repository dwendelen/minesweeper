package minesweeper

import minesweeper.neural.NeuralNetwork

object Config {
    //WORKING CONFIGURATIONS:
    //val NEURAL_NET: String = "data/neuralnet_1000.json"
    // val INITIAL_NETWORK_FACTORIES: () => List[NeuralNetwork] = () => List(
    //        NeuralNetwork.createRandom(100, List(1000, 1)),
    //         NeuralNetwork.createRandom(100, List(1000, 1))
    //     )
    //val NEURAL_NET: String = "data/neuralnet_1000__300_300.json"
    //val INITIAL_NETWORK_FACTORIES: () => List[NeuralNetwork] = () => List(
    //    NeuralNetwork.createRandom(100, List(1000, 1)),
    //    NeuralNetwork.createRandom(100, List(300, 300, 1))
    //)
    val STEP_FACTOR: Double = 0.01
    val DATA_SET: String = "data/dataset.json"
    //val NEURAL_NET: String = "data/neuralnet_1000.json"
   // val INITIAL_NETWORK_FACTORIES: () => List[NeuralNetwork] = () => List(
    //        NeuralNetwork.createRandom(100, List(1000, 1)),
   //         NeuralNetwork.createRandom(100, List(1000, 1))
   //     )

    val NEURAL_NET: String = "data/neuralnet_1000__300_300.json"
    val INITIAL_NETWORK_FACTORIES: () => List[NeuralNetwork] = () => List(
            NeuralNetwork.createRandom(100, List(1000, 1)),
            NeuralNetwork.createRandom(100, List(300, 300, 1))
        )
    val ignoreFirstNetwork = false
}
