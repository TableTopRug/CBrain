package tabletoprug.cbrain.models


class Layer {
    val neurons: MutableMap<String, Neuron> = mutableMapOf()


    fun addNeuron(neuron: Neuron) {}

    fun removeNeuron(neuron: Neuron) {}
}