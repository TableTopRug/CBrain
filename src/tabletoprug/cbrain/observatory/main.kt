package tabletoprug.cbrain.observatory

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tabletoprug.cbrain.models.FiringSquad
import tabletoprug.cbrain.observatory.models.Neuron
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds


fun main(): Unit = runBlocking {
    val squad = FiringSquad();

    for (i in 1..10) {
        val neuron = Neuron("n-$i")

        for (member in squad.neurons) {
            // Old member sends to new neuron
            neuron.connectFrom(member, Random.nextDouble())

            val value = Random.nextDouble();

            val min = minOf(maxOf(Random.nextDouble(), Double.MIN_VALUE), Double.MAX_VALUE - 500);

            if (value in min..(min + 500) && member.outgoing.size < 10) {
                member.connectFrom(neuron, Random.nextDouble())
            }
        }

        squad.addToSquad(neuron)

        // Launch work without blocking
       launch {
            delay(100.milliseconds) // non-blocking delay
            neuron.forceFire(0.9)
            println("Neuron fired: $neuron")
       }
    }

    delay(5000)
}