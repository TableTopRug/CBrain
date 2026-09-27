package tabletoprug.cbrain.observatory

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tabletoprug.cbrain.models.FiringSquad
import tabletoprug.cbrain.observatory.models.Neurode
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds


fun main(): Unit = runBlocking {
    val squad = FiringSquad();

    for (i in 1..10) {
        val neurode = Neurode("n-$i")

        for (member in squad.neurodes) {
            // Old member sends to new neuron
            neurode.connectFrom(member, Random.nextDouble())

            val value = Random.nextDouble();

            val min = minOf(maxOf(Random.nextDouble(), Double.MIN_VALUE), Double.MAX_VALUE - 500);

            if (value in min..(min + 500) && member.outgoing.size < 10) {
                member.connectFrom(neurode, Random.nextDouble())
            }
        }

        squad.addToSquad(neurode)

        // Launch work without blocking
       launch {
            delay(100.milliseconds) // non-blocking delay
            neurode.forceFire(0.9)
            println("Neuron fired: $neurode")
       }
    }

    delay(5000)
}