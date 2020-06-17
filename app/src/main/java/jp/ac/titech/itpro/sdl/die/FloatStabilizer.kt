package jp.ac.titech.itpro.sdl.die

class FloatStabilizer(
        private val latency: Float = 0.75f
) {
    var value: Float = 0.0f
        private set

    fun update(rawValue: Float) {
        value = latency * value + (1 - latency) * rawValue
    }
}