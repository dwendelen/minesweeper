package minesweeper.learn

import minesweeper.neural.NeuralNetwork

class LearningProcess(stepFactor: Double, var dataSet: DataSet, val neuralNetwork: NeuralNetwork) {
    def tick(): Unit = {
        if (dataSet.points.isEmpty) {
            Thread.sleep(100)
        } else {
            val index = (Math.random() * dataSet.points.size).toInt
            val dataPoint = dataSet.points(index)

            neuralNetwork.setInput(dataPoint.inputs)

            val values = neuralNetwork.evaluate()
            val error = dataPoint.output
                    .zip(values)
                    .map{case (dataPointOutput, value) => dataPointOutput - value}
                    .sum
            if (Math.random() < 0.001) {
                println(error)
            }
            neuralNetwork.learn(stepFactor * error)
        }
    }

    def addDataPoints(dataPoints: List[DataPoint]): Unit = {
        dataSet = DataSet(dataPoints ++ dataSet.points)
    }
}