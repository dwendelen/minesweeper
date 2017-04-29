package minesweeper.learn

import minesweeper.neural.NeuralNetwork

class LearningProcess(stepFactor: Double, var dataSet: DataSet, val neuralNetwork: NeuralNetwork) {
    def tick(): Unit = {
        if (dataSet.points.isEmpty) {
            Thread.sleep(100)
        } else {
            val index = (Math.random() * dataSet.points.size).toInt
            val dataPoint = dataSet.points(index)

            neuralNetwork.evaluate(dataPoint.inputs)
            val errors = neuralNetwork.learn(stepFactor, dataPoint.output)

            if (Math.random() < 0.001) {
                println(errors)
            }
        }
    }

    def addDataPoints(dataPoints: List[DataPoint]): Unit = {
        dataSet = DataSet(dataPoints ++ dataSet.points)
    }
}