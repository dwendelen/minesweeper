package minesweeper.store

import java.io.{File, FileOutputStream}

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
        val fle = new File(file)
        if(!fle.exists()) {
            fle.createNewFile()
        }
        objectMapper.writeValue(fle, networkDTO)
    }

    def writeToFile(dataSet: DataSet, file: String): Unit = {
        val dataSetDTO = dataSet.store()
        val fle = new File(file)
        if(!fle.exists()) {
            fle.createNewFile()
        }
        objectMapper.writeValue(fle, dataSetDTO)
    }

    def readDataSetFromFile(source: String, factory: () => DataSet): DataSet = {
        val file = new File(source)
        if (!file.exists()) {
            return factory.apply()
        }
        val dataSetDTO = objectMapper.readValue(file, classOf[DataSetDTO])
        val dataPoints = dataSetDTO.points
                .map(p => DataPoint(p.inputs, p.output))
        DataSet(dataPoints)
    }

    type InputMap = Map[Int, NeuronInput]

    def readNeuralNetFromFile(source: String, factory: () => NeuralNetwork): NeuralNetwork = {
        val file = new File(source)
        if (!file.exists()) {
            return factory.apply()
        }

        val networkDTO = objectMapper.readValue(file, classOf[NetworkDTO])
        val nbOfInputNodes = networkDTO.layers(0).neurons(0).weights.size
        val inputs = (0 until nbOfInputNodes)
                .map(_ => new Input)
                .toList

        val inputMap: InputMap = inputs
                .zipWithIndex
                .map { case (input, index) => (index, input) }
                .toMap
        val layers = processLayer(networkDTO.layers, Nil, inputMap)

        new NeuralNetwork(inputs, layers.slice(0, layers.size - 1), layers.last.last)
    }


    def processLayer(layers: List[LayerDTO], result: List[List[Neuron]], inputMap: InputMap): List[List[Neuron]] = layers match {
        case Nil => result
        case head :: tail =>
            val (newInputMap, neurons) = processNeurons(head.neurons, Nil, inputMap)
            processLayer(tail, result ++ List(neurons), newInputMap)
    }

    def processNeurons(neuronDtos: List[NeuronDTO], result: List[Neuron], inputMap: InputMap):
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