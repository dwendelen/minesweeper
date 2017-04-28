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
        fromFile(path, factory, mapNetwork, classOf[NetworkDTO])
    }

    private def fromFile[T, D](path: String, factory: () => T, mapper: D => T , clazz: Class[D]): T = {
        val file = new File(path)
        if (!file.exists()) {
            return factory.apply()
        }

        val dto = objectMapper.readValue(file, clazz)
        mapper(dto)
    }

    type InputMap = Map[Int, NeuronInput]

    private def mapNetwork(networkDTO: NetworkDTO) : NeuralNetwork = {
        val nbOfInputNodes = networkDTO
                .layers.head
                .neurons.head
                .weights
                .size
        val inputs = (0 until nbOfInputNodes)
                .map(_ => new Input)
                .toList

        val inputMap: InputMap = inputs
                .zipWithIndex
                .map (_.swap)
                .toMap
        val layers = processLayer(networkDTO.layers, Nil, inputMap)

        new NeuralNetwork(inputs, layers)
    }

    private def processLayer(layers: List[LayerDTO], result: List[List[Neuron]], inputMap: InputMap): List[List[Neuron]] = layers match {
        case Nil => result
        case head :: tail =>
            val (newInputMap, neurons) = processNeurons(head.neurons, Nil, inputMap)
            processLayer(tail, result ++ List(neurons), newInputMap)
    }

    private def processNeurons(neuronDtos: List[NeuronDTO], result: List[Neuron], inputMap: InputMap):
    (Map[Int, NeuronInput], List[Neuron]) = neuronDtos match {
        case Nil => (inputMap, result)
        case head :: tail =>
            val weight = new Weight(head.weight)
            val inputPairs = head.weights
                    .map { case (inputIndex: String, weight: Double) =>
                        val neuronInput = inputMap(inputIndex.toInt)
                        InputPair(neuronInput, new Weight(weight))
                    }
                    .toList
            val neuron = new Neuron(inputPairs, weight)
            processNeurons(tail, result ++ List(neuron), inputMap + (head.id -> neuron))
    }
}