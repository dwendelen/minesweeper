package minesweeper.learn

import minesweeper.store.{DataPointDTO, DataSetDTO}

case class DataSet(points: List[DataPoint]) {
    def store(): DataSetDTO = {
        val pointDTOs = points.map(_.store())
        DataSetDTO(pointDTOs)
    }
}

case class DataPoint(inputs: List[Double],
                     output: List[Double]) {
    def store(): DataPointDTO = {
        DataPointDTO(inputs, output)
    }
}