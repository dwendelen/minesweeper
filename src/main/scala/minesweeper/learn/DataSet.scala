package minesweeper.learn

import minesweeper.store.{DataPointDTO, DataSetDTO}

case class DataSet(var points: List[DataPoint]) {
    def add(point: DataPoint): Unit = {
        points = points ++ List(point)
    }

    def store(): DataSetDTO = {
        val pointDTOs = points.map(_.store())
        DataSetDTO(pointDTOs)
    }

    def random(): DataPoint = {
        val index = (Math.random() * points.size).toInt
        points(index)
    }
}

case class DataPoint(inputs: List[Double],
                     output: List[Double]) {
    def store(): DataPointDTO = {
        DataPointDTO(inputs, output)
    }
}