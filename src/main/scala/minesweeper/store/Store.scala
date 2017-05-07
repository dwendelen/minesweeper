package minesweeper.store

import java.io.File

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import minesweeper.learn.{DataPoint, DataSet}
import minesweeper.neural._

class Store {
    private val objectMapper = new ObjectMapper()
    objectMapper.registerModule(DefaultScalaModule)

    def writeToString(neuralNetworks: List[NeuralNetwork]): String = {
        val networkDTOs = neuralNetworks.map(_.store())
        objectMapper.writeValueAsString(networkDTOs)
    }

    def writeToFile(neuralNetworks: List[NeuralNetwork], file: String): Unit = {
        val networkDTO = neuralNetworks.map(_.store())
        toFile(networkDTO, file)
    }

    def writeToFile(dataSet: DataSet, file: String): Unit = {
        val dataSetDTO = dataSet.store()
        toFile(dataSetDTO, file)
    }

    private def toFile(obj: Any, fileName: String): Unit = {
        val temp = new File(fileName + ".tmp")
        temp.delete()
        objectMapper.writeValue(temp, obj)

        val file = new File(fileName)
        file.delete()
        temp.renameTo(file)
    }

    def readDataSetFromFile(path: String, factory: () => DataSet): DataSet = {
        fromFile(path, factory, mapDataSet, classOf[DataSetDTO])
    }

    private def mapDataSet(dataSetDTO: DataSetDTO): DataSet = {
        val dataPoints = dataSetDTO.points
            .map(p => DataPoint(p.inputs, p.output))
        DataSet(dataPoints)
    }

    type NetworkDTOs = List[List[List[List[Double]]]]

    def readNeuralNetFromFile(path: String, factory: () => List[NeuralNetwork]): List[NeuralNetwork] = {
        fromFile[List[NeuralNetwork], NetworkDTOs](path, factory, l => l.map(i => new NeuralNetwork(i)), classOf[NetworkDTOs])
    }

    private def fromFile[T, D](path: String, factory: () => T, mapper: D => T, clazz: Class[D]): T = {
        val file = new File(path)
        if (!file.exists()) {
            val tmpFile = new File(path + ".tmp")
            if (tmpFile.exists()) {
                tmpFile.renameTo(file)
            } else {
                return factory.apply()
            }
        }

        val dto = objectMapper.readValue(file, clazz)
        mapper(dto)
    }
}