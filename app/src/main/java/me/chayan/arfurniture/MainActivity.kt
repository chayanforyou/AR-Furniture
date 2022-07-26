package me.chayan.arfurniture

import android.os.Bundle
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import me.chayan.arfurniture.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var arFragment: ArFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fragment tag ( <fragment>) is not a view it act as a "container" for other views,
        // So you cannot access it like other views
        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment

        /**
         * Touch listener to detect when a user touches the ArScene plane to place a model
         */
        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            setModelOnUi(hitResult)
        }
    }


    /**
     * Used to load model and set it on ArScene where a user Taps
     */
    private fun setModelOnUi(hitResult: HitResult) {
        loadModel(R.raw.model) { modelRenderable ->
            //Used to get anchor point on scene where user tapped
            val anchor = hitResult.createAnchor()
            //Created an anchor node to attach the anchor with its parent
            val anchorNode = AnchorNode(anchor)
            //Added arSceneView as parent to the anchorNode. So our anchors will bind to arSceneView.
            anchorNode.setParent(arFragment.arSceneView.scene)

            //TransformableNode for out model. So that it can be rotated, scaled etc using gestures
            val transformableNode = TransformableNode(arFragment.transformationSystem)
            //Assigned anchorNode as parent so that our model stays at the position where user taps
            transformableNode.setParent(anchorNode)
            //Assigned the resulted model received from loadModel method to transformableNode
            transformableNode.renderable = modelRenderable
            //Sets this node as selected node by default
            transformableNode.select()
        }
    }

    /**
     * Used to load models from 'raw' with a callback when loading is complete
     */
    private fun loadModel(@RawRes model: Int, callback: (ModelRenderable) -> Unit) {
        ModelRenderable
            .builder()
            .setSource(this, model)
            .build()
            .thenAccept { modelRenderable ->
                callback(modelRenderable)
            }
    }
}