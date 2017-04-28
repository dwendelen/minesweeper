package minesweeper.store

import com.fasterxml.jackson.annotation.JsonCreator

import scala.beans.BeanProperty

case class NetworkDTO(@BeanProperty
                      layers: List[LayerDTO])

case class LayerDTO(@BeanProperty
                    neurons: List[NeuronDTO])

case class NeuronDTO(@BeanProperty
                     id: Int,
                     @BeanProperty
                     weight: Double,
                     @BeanProperty
                     weights: Map[String, Double])