package jp.ac.titech.itpro.sdl.die

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnSeekBarChangeListener {
    private val renderer = SimpleRenderer()
    private val cube = Cube()
    private val pyramid = Pyramid()

    private lateinit var angleListener: AngleListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_main)
        seekbar_x.max = 360
        seekbar_y.max = 360
        seekbar_z.max = 360
        seekbar_x.setOnSeekBarChangeListener(this)
        seekbar_y.setOnSeekBarChangeListener(this)
        seekbar_z.setOnSeekBarChangeListener(this)
        renderer.setObj(cube)
        gl_view.setRenderer(renderer)
        angleListener = AngleListener(this)
        angleListener.onAngleChanged { x, y, z ->
            Log.d(TAG, "onAngleChanged: x=$x, y=$y, z=$z")
            renderer.rotateObjX(y)
            renderer.rotateObjY(-z)
            renderer.rotateObjZ(x)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        gl_view.onResume()
        angleListener.resume()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        gl_view.onPause()
        angleListener.pause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu")
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected")
        when (item.itemId) {
            R.id.menu_cube -> renderer.setObj(cube)
            R.id.menu_pyramid -> renderer.setObj(pyramid)
        }
        return true
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when (seekBar.id) {
            R.id.seekbar_x -> renderer.rotateObjX(progress.toFloat())
            R.id.seekbar_y -> renderer.rotateObjY(progress.toFloat())
            R.id.seekbar_z -> renderer.rotateObjZ(progress.toFloat())
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}