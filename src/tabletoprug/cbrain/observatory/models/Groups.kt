 package tabletoprug.cbrain.models

 import tabletoprug.cbrain.observatory.models.Neuron


 class FiringSquad {
     private val _neurons = mutableListOf<Neuron>()

     val neurons: List<Neuron>
         get() = _neurons                  // live view (recommended)

     // or if you prefer a copy each time:
     // get() = _neurons.toList()

     fun addToSquad(n: Neuron) {
         if (n !in _neurons) {
             _neurons.add(n)
         }
     }

     fun removeFromSquad(n: Neuron) {
         _neurons.remove(n)
     }
 }