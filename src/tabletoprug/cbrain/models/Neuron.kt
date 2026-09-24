package tabletoprug.cbrain.models

import tabletoprug.cbrain.ActivationType
import kotlin.math.exp
import kotlin.math.tanh


class Neuron(
    val id: String,
    var bias: Double = 0.0,
    var activation: ActivationType = ActivationType.RELU
) {
    // sourceId → weight
    val connections = mutableMapOf<String, Double>()

    // runtime value after forward pass
    var value: Double = 0.0
        private set

    fun connect(from: Neuron, weight: Double) {
        connections[from.id] = weight
    }

    fun activate(inputs: Map<String, Double>) {
        val sum = connections.entries.sumOf { (srcId, w) ->
            w * (inputs[srcId] ?: 0.0)
        } + bias

        value = when (activation) {
            ActivationType.LINEAR  -> sum
            ActivationType.RELU    -> maxOf(0.0, sum)
            ActivationType.SIGMOID -> 1.0 / (1.0 + exp(-sum))
            ActivationType.TANH    -> tanh(sum)
        }
    }
}