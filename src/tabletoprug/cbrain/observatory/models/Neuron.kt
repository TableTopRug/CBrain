package tabletoprug.cbrain.observatory.models

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tabletoprug.cbrain.observatory.ActivationType
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.tanh
import kotlin.time.Duration.Companion.milliseconds


class Neuron(
    val id: String,
    var bias: Double = 0.0,
    var activation: ActivationType = ActivationType.RELU,
    var baseThreshold: Double = 1.0,
    var impact: Double = 1.0,
    var postFireDecay: Double = 0.5,

    // NEW: habituation controls
    var fireCountThreshold: Int = 5,        // after this many inputs from one source, start decaying it
    var sourceDecayRate: Double = 0.9,       // multiplier applied per over-threshold fire (e.g. 0.9 = 10% weaker each time)
    var sourceDecayFloor: Double = 0.05,     // don't let a source's influence decay to literally zero

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    val incoming = mutableMapOf<String, Double>()
    val outgoing = mutableListOf<Neuron>()

    private val sourceFireCounts = mutableMapOf<String, Int>()
    private val sourceDecayFactor = mutableMapOf<String, Double>()

    var accumulated: Double = 0.0
    var value: Double = 0.0
    var hasFired: Boolean = false
        private set

    val threshold: Double
        get() = baseThreshold * impact + bias


    fun connectFrom(source: Neuron, weight: Double) {
        incoming[source.id] = weight
        source.outgoing.add(this)
    }

    fun reset() {
        accumulated = 0.0
        value = 0.0
        hasFired = false
    }

    fun resetHabituation(sourceId: String? = null) {
        if (sourceId == null) {
            sourceFireCounts.clear()
            sourceDecayFactor.clear()
        } else {
            sourceFireCounts.remove(sourceId)
            sourceDecayFactor.remove(sourceId)
        }
    }

    fun receive(sourceId: String, sourceValue: Double) {
        val weight = incoming[sourceId] ?: return

        // 1. Update this source's fire count
        val count = (sourceFireCounts[sourceId] ?: 0) + 1
        sourceFireCounts[sourceId] = count

        // 2. Once past threshold, keep shrinking this source's multiplier
        var factor = sourceDecayFactor[sourceId] ?: 1.0
        if (count > fireCountThreshold) {
            factor = (factor * sourceDecayRate).coerceAtLeast(sourceDecayFloor)
            sourceDecayFactor[sourceId] = factor
        }

        // 3. Apply weight * habituation factor, not just raw weight
        accumulated += weight * factor * sourceValue

        if (!hasFired && accumulated >= threshold) {
            fire()
        }
    }

    private fun fire() {
        if (hasFired) return
        hasFired = true

        value = when (activation) {
            ActivationType.LINEAR  -> accumulated
            ActivationType.RELU    -> max(0.0, accumulated)
            ActivationType.SIGMOID -> 1.0 / (1.0 + exp(-accumulated))
            ActivationType.TANH    -> tanh(accumulated)
        }

        accumulated = (accumulated - postFireDecay).coerceAtLeast(0.0)

        scope.launch {
            for (target in outgoing) {
                target.receive(id, value)
            }
        }

        println("[$id] FIRED | value=${"%.3f".format(value)} | accumulated after decay=${"%.3f".format(accumulated)} | threshold was ${"%.3f".format(threshold)} | outgoing=${outgoing.size}")

        scope.launch {
            delay(50.milliseconds)   // refractory period — tune as you like
            hasFired = false
        }
    }

    fun forceFire(inputValue: Double) {
        accumulated = inputValue
        fire()
    }

    override fun toString(): String {
        return "Neuron($id, acc=${"%.3f".format(accumulated)}, " +
                "val=${"%.3f".format(value)}, fired=$hasFired)"
    }
}