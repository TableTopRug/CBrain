package tabletoprug.cbrain.observatory.playground


data class NeuralSignal(
    val contentType: String,        // "visual", "audio", "threat", "internal", etc.
    val urgency: Float,            // 0.0 – 1.0
    val importance: Float,
    val processingCost: Double,     // how expensive is this?
    val confidence: Double,
    val payload: Any? = null        // optional actual data (or reference)
)

class Block {
    private val absoluteClock: AbsoluteBlockClock = mutableMapOf()  // neuronUniqueId -> count, includes dead
    private val livingNeurons: Set<NeurodeId> = mutableSetOf()        // neuronUniqueId, alive only

    fun relativeClock(): Map<Long, Int> =
        absoluteClock.filterKeys { it in livingNeurons }

    fun onNeuronCreated(id: Long) {
        livingNeurons.plus(id)
        absoluteClock[id] = 0
    }

    fun onNeuronEvent(id: Long) {
        absoluteClock[id] = (absoluteClock[id] ?: 0) + 1
    }

    fun onNeuronDied(id: Long) {
        livingNeurons.remove(id)
        // absoluteClock entry stays, frozen at last value
    }
}