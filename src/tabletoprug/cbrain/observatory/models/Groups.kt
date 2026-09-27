 package tabletoprug.cbrain.models

 import tabletoprug.cbrain.observatory.models.Neurode


 class FiringSquad {
     private val _neurodes = mutableListOf<Neurode>()

     val neurodes: List<Neurode>
         get() = _neurodes                  // live view (recommended)

     // or if you prefer a copy each time:
     // get() = _neurons.toList()

     fun addToSquad(n: Neurode) {
         if (n !in _neurodes) {
             _neurodes.add(n)
         }
     }

     fun removeFromSquad(n: Neurode) {
         _neurodes.remove(n)
     }
 }