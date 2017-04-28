package minesweeper.store

import scala.beans.BeanProperty

case class DataSetDTO(@BeanProperty
                      points: List[DataPointDTO])

case class DataPointDTO(@BeanProperty
                        inputs: List[Double],
                        @BeanProperty
                        output: List[Double])