package minesweeper.store

import java.io.File

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import minesweeper.learn.{DataPoint, DataSet}
import minesweeper.neural._

class Store {
    private val objectMapper = new ObjectMapper()
    objectMapper.registerModule(DefaultScalaModule)

    def writeToString(neuralNetwork: NeuralNetwork): String = {
        val networkDTO = neuralNetwork.store()
        objectMapper.writeValueAsString(networkDTO)
    }

    def writeToFile(neuralNetwork: NeuralNetwork, file: String): Unit = {
        val networkDTO = neuralNetwork.store()
        toFile(networkDTO, file)
    }

    def writeToFile(dataSet: DataSet, file: String): Unit = {
        val dataSetDTO = dataSet.store()
        toFile(dataSetDTO, file)
    }

    private def toFile(obj: Any, file: String): Unit = {
        val fle = new File(file)
        if (!fle.exists()) {
            fle.createNewFile()
        }
        objectMapper.writeValue(fle, obj)
    }

    def readDataSetFromFile(path: String, factory: () => DataSet): DataSet = {
        fromFile(path, factory, mapDataSet, classOf[DataSetDTO])
    }

    private def mapDataSet(dataSetDTO: DataSetDTO): DataSet = {
        val dataPoints = dataSetDTO.points
                .map(p => DataPoint(p.inputs, p.output))
        DataSet(dataPoints)
    }

    def readNeuralNetFromFile(path: String, factory: () => NeuralNetwork): NeuralNetwork = {
        fromFile(path, factory, l => new NeuralNetwork(l), classOf[List[List[List[Double]]]])
    }

    private def fromFile[T, D](path: String, factory: () => T, mapper: D => T, clazz: Class[D]): T = {
        val file = new File(path)
        if (!file.exists()) {
            return factory.apply()
        }

        val dto = objectMapper.readValue(file, clazz)
        mapper(dto)
    }
}