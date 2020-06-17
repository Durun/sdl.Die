package jp.ac.titech.itpro.sdl.die

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class AngleListener(context: Activity) : SensorEventListener {
    private val handlers = mutableListOf<(Float, Float, Float) -> Unit>()

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private val angleX = FloatStabilizer()
    private val angleY = FloatStabilizer()
    private val angleZ = FloatStabilizer()

    fun onAngleChanged(handler: (x: Float, y: Float, z: Float) -> Unit) {
        handlers.add(handler)
    }

    fun resume() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_GAME
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                    this,
                    magneticField,
                    SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    fun pause() {
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
            Sensor.TYPE_MAGNETIC_FIELD -> System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }

        SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometerReading,
                magnetometerReading
        )
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        val pi = Math.PI.toFloat()
        val x = orientationAngles[0] * 180 / pi
        val y = orientationAngles[1] * 180 / pi
        val z = orientationAngles[2] * 180 / pi
        angleX.update(x)
        angleY.update(y)
        angleZ.update(z)
        handlers.forEach { handler -> handler(angleX.value, angleY.value, angleZ.value) }
    }
}