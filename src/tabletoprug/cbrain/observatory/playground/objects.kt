package tabletoprug.cbrain.observatory.playground


object NeuronIdGenerator {
    private val counter = java.util.concurrent.atomic.AtomicLong(0)
    fun next(): Long = counter.incrementAndGet()
}