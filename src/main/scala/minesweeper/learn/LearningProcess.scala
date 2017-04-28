package minesweeper.learn

import minesweeper.neural.NeuralNetwork

class LearningProcess(stepFactor: Double, var dataSet: DataSet, val neuralNetwork: NeuralNetwork) {
    def tick() = {
        if (dataSet.points.isEmpty) {
            Thread.sleep(100)
        } else {
            val index = (Math.random() * dataSet.points.size).toInt
            val point = dataSet.points(index)

            point.inputs
                    .zipWithIndex
                    .foreach { case (input, index) =>
                        neuralNetwork.setInput(index, input)
                    }

            val value = neuralNetwork.evaluate()
            val difference = point.output - value
            neuralNetwork.learn(stepFactor * difference)
        }
    }

    def addDataPoint(dataPoint: DataPoint): Unit = {
        dataSet = DataSet(dataPoint :: dataSet.points)
    }
}
