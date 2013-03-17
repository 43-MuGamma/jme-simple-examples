/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simpleEditor;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;

/**
 *
 * @author mifth
 */
public class EditorCameraManager {

    private Camera cam;
    private Node camTrackHelper;
    private InputManager imputMan;
    private EditorChaseCamera chaseCam;
    private Application app;
    private EditorBaseManager base;
    private float camMoveSpeed;
    private AssetManager assetManager;

    public EditorCameraManager(Application app, EditorBaseManager base) {

        this.app = app;
        this.base = base;
        cam = this.app.getCamera();
        camTrackHelper = (Node) this.app.getViewPort().getScenes().get(0);
        camTrackHelper = (Node) camTrackHelper.getChild("camTrackHelper");
        imputMan = this.app.getInputManager();
        assetManager = this.app.getAssetManager();

        camMoveSpeed = 0.006f;
//        this.cam.setFrustumFar(1000);
        this.cam.setFrustumPerspective(30f, (float)this.cam.getWidth()/(float)this.cam.getHeight(), 0.3f, 10000f);
        this.cam.updateViewProjection();
        this.cam.update();
//        this.cam.setFrustumNear(0.1f);

        setCameraNow();
//        setOrtho(true);

    }

    private void setCameraNow() {

        // Enable a chase cam
        chaseCam = new EditorChaseCamera(cam, camTrackHelper, imputMan);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setTrailingEnabled(false);

//        chaseCam.setMinVerticalRotation(-FastMath.PI * 0.499f);
//        chaseCam.setMaxVerticalRotation(FastMath.PI * 0.499f);

        chaseCam.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        chaseCam.setRotationSpeed(2f);
        
        chaseCam.setMinDistance(0.05f);
        chaseCam.setMaxDistance(5000);
        chaseCam.setDefaultDistance(300);
//        chaseCam.setZoomSensitivity(5f);

    }

    protected float getCamMoveSpeed() {
        return camMoveSpeed;
    }

    protected void setCamMoveSpeed(float camMoveSpeed) {
        this.camMoveSpeed = camMoveSpeed;
    }
    
    protected Node getCamTrackHelper() {
        return camTrackHelper;
    }    

    protected void moveCamera() {

        // center of the screen
        float width = cam.getWidth() * 0.5f;
        float height = cam.getHeight() * 0.5f;
        Vector2f ceneterScr = new Vector2f(width, height);

        Vector2f endPosMouse = app.getInputManager().getCursorPosition();
        float mouseDist = ceneterScr.distance(endPosMouse);

        Vector3f camMoveX = cam.getLeft();
        camMoveX.negateLocal();
        camMoveX.normalizeLocal();

        Vector3f camMoveY = cam.getUp();
        camMoveY.normalizeLocal();
        
        Vector3f vecToMove = camMoveX.mult((endPosMouse.x - ceneterScr.x) / cam.getWidth());
        vecToMove.addLocal(camMoveY.mult((endPosMouse.y - ceneterScr.y) / cam.getHeight())).normalizeLocal();
        vecToMove.multLocal(mouseDist * camMoveSpeed * (0.5f + (chaseCam.getDistanceToTarget()*0.005f)));
//        System.out.println("target" + chaseCam.getDistanceToTarget());
//        System.out.println(mouseDist * camMoveSpeed);
        
        camTrackHelper.move(vecToMove);
    }
    
     protected void moveCameraToSelection() {
         
     }

    protected void setCamTracker() {

        // Red line for X axis
        final Line xAxis = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0.1f, 0f, 0f));
        xAxis.setLineWidth(2f);
        Geometry gxAxis = new Geometry("XAxis", xAxis);
        gxAxis.setModelBound(new BoundingBox());
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", new ColorRGBA(1.0f, 0.0f, 0.0f, 0.2f));
        mat1.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        gxAxis.setQueueBucket(RenderQueue.Bucket.Transparent);
        gxAxis.setShadowMode(RenderQueue.ShadowMode.Off);
        gxAxis.setMaterial(mat1);
//        gxAxis.setCullHint(CullHint.Never);

        camTrackHelper.attachChild(gxAxis);


        // Blue line for Y axis
        final Line yAxis = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0.1f, 0f));
        yAxis.setLineWidth(2f);
        Geometry gyAxis = new Geometry("ZAxis", yAxis);
        gyAxis.setModelBound(new BoundingBox());
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", new ColorRGBA(0.0f, 0.0f, 1.0f, 0.2f));
        mat2.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        gyAxis.setQueueBucket(RenderQueue.Bucket.Transparent);
        gyAxis.setShadowMode(RenderQueue.ShadowMode.Off);
        gyAxis.setMaterial(mat2);
//        gzAxis.setCullHint(CullHint.Never);
        camTrackHelper.attachChild(gyAxis);


        // Blue line for Z axis
        final Line zAxis = new Line(new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0.1f));
        zAxis.setLineWidth(2f);
        Geometry gzAxis = new Geometry("ZAxis", zAxis);
        gzAxis.setModelBound(new BoundingBox());
        Material mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat3.setColor("Color", new ColorRGBA(0.0f, 1.0f, 0.0f, 0.2f));
        mat3.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        gxAxis.setQueueBucket(RenderQueue.Bucket.Transparent);
        gzAxis.setShadowMode(RenderQueue.ShadowMode.Off);
        gzAxis.setMaterial(mat3);
//        gzAxis.setCullHint(CullHint.Never);
        camTrackHelper.attachChild(gzAxis);

    }    
    
    protected void setOrtho(boolean bool) {

        if (bool == true) {


//         Camera cam2 = cam.clone(); 

            cam.setParallelProjection(true);
            float aspect = (float) cam.getWidth() / (float) cam.getHeight();
            float frustumSize = chaseCam.getDistanceToTarget();
            cam.setFrustum(-cam.getFrustumFar(), cam.getFrustumFar(), -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);

        } else if (bool == false) {

            cam.setParallelProjection(false);
        }

    }
}
