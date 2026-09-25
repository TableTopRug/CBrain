package tabletoprug.cbrain.observatory.playground


data class NeuralSignal(
    val contentType: String,        // "visual", "audio", "threat", "internal", etc.
    val urgency: Float,            // 0.0 – 1.0
    val importance: Float,
    val processingCost: Double,     // how expensive is this?
    val confidence: Double,
    val payload: Any? = null        // optional actual data (or reference)
)