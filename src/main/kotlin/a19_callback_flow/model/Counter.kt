package a19_callback_flow.model



class Counter {
    private val listeners: MutableList<OnChangedListener> = mutableListOf()
    var count = 0
        private set(value) {
            field = value
            notifyOnChanged(value)
        }

    fun increment() {
        count++
    }

    fun decrement() {
        count--
    }

    fun addOnChangedListener(listener: OnChangedListener) {
        listeners.add(listener)
    }

    fun removeOnChangedListener(listener: OnChangedListener){
        listeners.remove(listener)
    }

    private fun notifyOnChanged(count: Int) {
        listeners.forEach { listener -> listener.onChanged(count) }
    }
}