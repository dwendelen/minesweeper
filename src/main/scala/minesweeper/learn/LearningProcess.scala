package minesweeper.learn

import minesweeper.neural.NeuralNetwork

class LearningProcess(stepFactors: List[Double], var dataSet: DataSet, val neuralNetworks: List[NeuralNetwork]) {
    def tick(): List[Double] = {
        if (dataSet.points.isEmpty) {
            Thread.sleep(100)
            tick()
        } else {
            tickWithData()
        }
    }

    private def tickWithData(): List[Double] = {
        val dataPoint = dataSet.random()
        (neuralNetworks, dataPoint.output, stepFactors)
            .zipped
            .map {
                case (neuralNetwork, out, stepFactor) => tick(dataPoint.inputs, neuralNetwork, out, stepFactor)
            }
    }

    private def tick(input: List[Double], neuralNetwork: NeuralNetwork, out: Double, stepFactor: Double): Double = {
        neuralNetwork.evaluate(input)
        neuralNetwork.learn(stepFactor, out)
    }
}