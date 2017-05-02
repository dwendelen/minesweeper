package minesweeper.learn

import minesweeper.neural.NeuralNetwork

class LearningProcess(stepFactors: List[Double], var dataSet: DataSet, val neuralNetworks: List[NeuralNetwork]) {
    def tick(): List[Double] = {
        if (dataSet.points.isEmpty) {
            Thread.sleep(100)
            tick()
        } else {
            val dataPoint = dataSet.random()
            val out = dataPoint.output(index)

            if (out == 0) {
                tick()
            } else {
                neuralNetwork.evaluate(dataPoint.inputs)
                neuralNetwork.learn(stepFactor, List(out))
            }
        }
    }
}