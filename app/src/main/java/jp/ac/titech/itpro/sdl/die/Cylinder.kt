package jp.ac.titech.itpro.sdl.die

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class Cylinder(
        private val resolution: Int
) : Obj {
    private val vBuf: FloatBuffer
    private val topIndex: Int
    private val bottomIndex: Int
    private val sideIndices: List<Int>
    private val sideNormals: List<Triple<Float, Float, Float>>

    init {
        sideIndices = (0 until resolution).map { it * 4 }
        val sideVertices = (0 until resolution).flatMap {
            val th1 = 2 * Math.PI * it / resolution
            val th2 = 2 * Math.PI * (it + 1) / resolution
            val x1 = cos(th1)
            val z1 = sin(th1)
            val x2 = cos(th2)
            val z2 = sin(th2)
            listOf(
                    x1, 1.0, z1,
                    x1, -1.0, z1,
                    x2, -1.0, z2,
                    x2, 1.0, z2
            )
        }
        sideNormals = (0 until resolution).map {
            val th = 2 * Math.PI * (0.5 + it) / resolution
            val x = cos(th).toFloat()
            val y = 0.0f
            val z = sin(th).toFloat()
            Triple(x,y,z)
        }

        topIndex = sideIndices.last() + 4
        val topVertices = (0 until resolution).flatMap {
            val th = 2 * Math.PI * it / resolution
            val x = cos(th)
            val y = 1.0
            val z = sin(th)
            listOf(x, y, z)
        }
        bottomIndex = topIndex + resolution
        val bottomVertices = (0 until resolution).flatMap {
            val th = 2 * Math.PI * it / resolution
            val x = cos(th)
            val y = -1.0
            val z = sin(th)
            listOf(x, y, z)
        }
        val vertices = (sideVertices + topVertices + bottomVertices).map { it.toFloat() }

        vBuf = ByteBuffer
                .allocateDirect(vertices.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        vBuf.put(vertices.toFloatArray())
        vBuf.position(0)
    }

    override fun draw(gl: GL10) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vBuf)

        gl.glNormal3f(0.0f, 1.0f, 0.0f)
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, topIndex, resolution)

        gl.glNormal3f(0.0f, -1.0f, 0.0f)
        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, bottomIndex, resolution)

        sideIndices.forEachIndexed { index, it ->
            val n = sideNormals[index]
            gl.glNormal3f(n.first, n.second, n.third)
            gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, it, 4)
        }
    }
}