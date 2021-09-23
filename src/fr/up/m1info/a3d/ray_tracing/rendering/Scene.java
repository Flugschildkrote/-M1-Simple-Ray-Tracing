package fr.up.m1info.a3d.ray_tracing.rendering;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4;

import fr.up.m1info.a3d.ray_tracing.Color;
import fr.up.m1info.a3d.ray_tracing.Light;
import fr.up.m1info.a3d.ray_tracing.Material;
import fr.up.m1info.a3d.ray_tracing.Ray;
import fr.up.m1info.a3d.ray_tracing.Texture;
import fr.up.m1info.a3d.ray_tracing.Vec2f;
import fr.up.m1info.a3d.ray_tracing.Vec3f;
import fr.up.m1info.a3d.ray_tracing.shapes.Plane;
import fr.up.m1info.a3d.ray_tracing.shapes.Shape;
import fr.up.m1info.a3d.ray_tracing.shapes.Shape.IntersectionResult;
import fr.up.m1info.a3d.ray_tracing.shapes.Sphere;
import fr.up.m1info.a3d.ray_tracing.shapes.Triangle;

/**
 * Class to represent the scene. It includes all the objects to display, in this case a room
 * @author Philippe Meseure
 * @version 1.0
 */
public class Scene implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{
	
	byte[] pixelBuffer;
	ByteBuffer bytePixelBuffer;
	
	int textureId;
	int vboId, vaoId;
	private List<Shape> shapes;
	private List<Light> ligths;


	private final static float RESOLUTION_SCALE = 1.0f;
	private final static int PICTURE_WIDTH = (int)((float)1024*RESOLUTION_SCALE);
	private final static int PICTURE_HEIGHT = (int)((float)768*RESOLUTION_SCALE);
	private final static Vec3f AMBIANT_COLOR = new Vec3f(0.1f, 0.1f, 0.1f);
	
	private final static float LIGHT_C_ATT = 1.0f;
	private final static float LIGHT_L_ATT = 0.2f;
	private final static float LIGHT_Q_ATT = 0.05f;
	private final static Vec3f VOID_COLOR = new Vec3f(0.4, 0.4f, 0.5f);
	
	private final static int RAYCAST_ORDER = 15;
	private final static double DEFAULT_EPSILON = 10e-6; 

	
	float projectionPlaneDistance = 1.0f;
	Matrix4 cameraTransform;

	
	// Events ---------------------------
	float mouseWheelRot = 0;
	int mouseDx = 0, mouseDy = 0, mouseLastX = 0, mouseLastY = 0;
	int camDirX = 0, camDirZ = 0;
	Map<Integer, Boolean> pressMap;
	public static final float ZOOM_SPEED = 0.5f;
	public static final float CAMERA_MOVE_SPEED = 1.2f;
	public static final float CAMERA_ROT_SPEED = 0.05f; // rad/s
	
	//private final int 
	
	long lastFrame = 0;
	int frameCount = 0;
	float fpsTimer = 0;
	
	
	ThreadPoolExecutor executor;
	

	/**
	 * Constructor : build each wall, the floor and the ceiling as quads
	 * @throws IOException 
	 */
	public Scene() throws IOException
	{
		// Init observer's view angles		
		this.shapes = new ArrayList<>();
		this.ligths = new ArrayList<Light>();
		this.pressMap = new HashMap<Integer, Boolean>();
		pressMap.put(KeyEvent.VK_LEFT, false);
		pressMap.put(KeyEvent.VK_RIGHT, false);
		pressMap.put(KeyEvent.VK_UP, false);
		pressMap.put(KeyEvent.VK_DOWN, false);
		pressMap.put(KeyEvent.VK_CONTROL, false);
		pressMap.put(KeyEvent.VK_SHIFT, false);
		pressMap.put(KeyEvent.VK_A, false);


		this.cameraTransform = new Matrix4();
		this.cameraTransform.loadIdentity();
		this.cameraTransform.translate(0.0f, 2.0f, 0.0f);
		
		Material material = new Material();
		material.setShininess(1000);
		material.setSpecularFactor(0.8f);
		
		
		Material transparentMaterial = new Material();
		transparentMaterial.setShininess(1000);
		transparentMaterial.setSpecularFactor(0.2f);
		transparentMaterial.setRefractionFactor(1.5f);
		transparentMaterial.setOpacity(0.2f);
		
		
		Material floorMaterial = new Material();
		floorMaterial.setShininess(50.0f);
		//floorMaterial.setDiffuseColor(new Vec3f(Color.RED));
		floorMaterial.setDiffuseTexture(new Texture("res/pictures/tiles1.jpg"));
		floorMaterial.setSpecularFactor(0.4f);
		
		
		Material trisMaterial = new Material();
		trisMaterial.setShininess(1000);
		trisMaterial.setDiffuseTexture(new Texture("res/pictures/wall.jpg"));
		
		this.shapes.add(new Sphere(new Vec3f(-0.2f, 0.5f, -5.0f), 0.2f, material));
		this.shapes.add(new Sphere(new Vec3f(-1.0f, 1.0f, -5.0f), 0.5f, transparentMaterial));
		this.shapes.add(new Sphere(new Vec3f( 1.2f, 2.0f, -1.0f), 1.0f, material));

		Triangle tris = new Triangle(new Vec3f(0, 0, -5), new Vec3f(1, 0, -5), new Vec3f(1, 1, -5), trisMaterial);
		System.out.println(tris);
		this.shapes.add(tris);
		
		Plane ground = new Plane(new Vec3f(0.0f, 1.0f, 0.0f), new Vec3f(0, 0, 0), new Vec3f(0.1, 0, 0), new Vec3f(0, 0, 0.1), floorMaterial);
		//ground.setTiling(new Vec2f(2.0f, 2.0f));
		this.shapes.add(ground);
		
		Light l1 = new Light(Color.WHITE, new Vec3f(0.0f, 2.0f, -10.0f));
		this.ligths.add(l1);
		this.ligths.add(new Light(Color.WHITE, new Vec3f(-0.5f, 5.0f, -3.0f)));
		this.ligths.add(new Light(Color.BLUE, new Vec3f(1.5f, 10.0f, -3.0f)));
		//l1.
		
		initSkybox();
		
		lastFrame = System.currentTimeMillis();
		
		int nbCores = Runtime.getRuntime().availableProcessors();
		MainActivity.log("Nb cores=" + nbCores);
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbCores);

	}
	
	private void initSkybox() throws IOException {
		
		double dist = 100;
		double hdist = dist/2;

		double idist = 1/(dist);
		
		List<Material> skyboxMaterials = new ArrayList<>();
		
		
		Material skyboxTopMat = new Material();
		skyboxTopMat.setDiffuseTexture(new Texture("res/pictures/skybox/TOP.jpg"));
		this.shapes.add(new Plane(new Vec3f(0.0f, 1.0f, 0.0f), new Vec3f(hdist, hdist, hdist), new Vec3f(0, 0, -idist), new Vec3f(-idist, 0, 0), skyboxTopMat));
		
		Material skyboxFrontMat = new Material();
		skyboxFrontMat.setDiffuseTexture(new Texture("res/pictures/skybox/FRONT.jpg"));
		this.shapes.add(new Plane(new Vec3f(0, 0, 1), new Vec3f(hdist, -hdist, -hdist), new Vec3f(idist, 0, 0), new Vec3f(0, -idist, 0), skyboxFrontMat));
		
		Material skyboxBackMat = new Material();
		skyboxBackMat.setDiffuseTexture(new Texture("res/pictures/skybox/BACK.jpg"));
		this.shapes.add(new Plane(new Vec3f(0, 0, -1), new Vec3f(hdist, -hdist, hdist), new Vec3f(-idist, 0, 0), new Vec3f(0, -idist, 0), skyboxBackMat));
		
		
		Material skyboxLeftMat = new Material();
		skyboxLeftMat.setDiffuseTexture(new Texture("res/pictures/skybox/LEFT.jpg"));
		this.shapes.add(new Plane(new Vec3f(1, 0, 0), new Vec3f(-hdist, -hdist, hdist), new Vec3f(0, 0, -idist), new Vec3f(0, idist, 0), skyboxLeftMat));
		
		Material skyboxRighttMat = new Material();
		skyboxRighttMat.setDiffuseTexture(new Texture("res/pictures/skybox/RIGHT.jpg"));
		this.shapes.add(new Plane(new Vec3f(-1, 0, 0), new Vec3f(hdist, -hdist, hdist), new Vec3f(0, 0, idist), new Vec3f(0, -idist, 0), skyboxRighttMat));
		
		skyboxMaterials.add(skyboxTopMat);
		skyboxMaterials.add(skyboxFrontMat);
		skyboxMaterials.add(skyboxBackMat);
		skyboxMaterials.add(skyboxLeftMat);
		skyboxMaterials.add(skyboxRighttMat);

		
		for(Material m : skyboxMaterials) {
			m.setShininess(1);
			m.setSpecularColor(Color.BLACK);	
			m.setDiffuseColor(new Vec3f(1, 1, 1));
			m.setIgnoreLight(true);
		}
	}
	


	/**
	 * Init some OpenGL and shaders uniform data to render the simulation scene
	 * @param renderer Renderer
	 */
	public void initGraphics(MyGLRenderer renderer)
	{
		GL3 gl=renderer.getGL();

		MainActivity.log("Initializing graphics");
		// Set the background frame color
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		// Allow back face culling !!
		gl.glEnable(GL3.GL_CULL_FACE);
		
		int[] buffers = new int[3];
		final int VBO_INDEX = 0;
		final int VAO_INDEX = 1;
		final int TEXTURE_INDEX = 2;
		
		
		MainActivity.log("Initializing texture");
		
		gl.glGenBuffers(1, buffers, VBO_INDEX);
		gl.glGenVertexArrays(1, buffers, VAO_INDEX);
		gl.glGenTextures(1, buffers, TEXTURE_INDEX);
		vboId = buffers[VBO_INDEX];
		vaoId = buffers[VAO_INDEX];
		textureId = buffers[TEXTURE_INDEX];
		
		gl.glActiveTexture(GL3.GL_TEXTURE0);
		gl.glBindTexture(GL3.GL_TEXTURE_2D, textureId);
		gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_NEAREST);
		gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_NEAREST);

		gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA, PICTURE_WIDTH, PICTURE_HEIGHT, 0, GL3.GL_RGBA, GL3.GL_UNSIGNED_BYTE, null);
		
		float[] vboData = {
			-1.0f, -1.0f,   	1.0f, -1.0f,   	1.0f, 1.0f,
			-1.0f,  1.0f,      -1.0f, -1.0f,    1.0f, 1.0f,
		};
		
		FloatBuffer vboBuffer = FloatBuffer.wrap(vboData);
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vboId);
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, vboBuffer.limit()*Float.BYTES, vboBuffer, GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
		
		gl.glBindVertexArray(vaoId);
			gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vboId);
		    gl.glEnableVertexAttribArray(0);
			gl.glVertexAttribPointer(0, 2, GL3.GL_FLOAT, false, 0, 0);
		    gl.glEnableVertexAttribArray(1);
			gl.glVertexAttribPointer(1, 2, GL3.GL_FLOAT, false, 0, 0);
		gl.glBindVertexArray(0);
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
		
		pixelBuffer = new byte[PICTURE_WIDTH*PICTURE_HEIGHT*4];
		bytePixelBuffer = ByteBuffer.wrap(pixelBuffer);
		
		MainActivity.log("Graphics initialized");
	}


	/**
	 * Make the scene evoluate, to produce an animation for instance
	 * Here, only the viewer rotates
	 */
	public void step()
	{
	}
	
	
	private void processEvents(float frametime) {
		projectionPlaneDistance += (mouseWheelRot*frametime*ZOOM_SPEED);
		projectionPlaneDistance = Math.min(100.0f, Math.max(0.1f, projectionPlaneDistance));
		mouseWheelRot = 0;
		
		float moveX = ((float)camDirX) * frametime * CAMERA_MOVE_SPEED;
		float moveZ = ((float)camDirZ) * frametime * CAMERA_MOVE_SPEED;
		float vAngle = -mouseDy * frametime * CAMERA_ROT_SPEED;
		float hAngle = -mouseDx * frametime * CAMERA_ROT_SPEED;

		Matrix4 cameraTransformI = new Matrix4();
		cameraTransformI.multMatrix(cameraTransform);
		cameraTransformI.invert();
		float[] axis = { 0.0f, 1.0f, 0.0f, 0.0f };
		float[] resAxis = new float[4];
		cameraTransformI.multVec(axis, resAxis);
		cameraTransform.rotate(hAngle, resAxis[0], resAxis[1], resAxis[2]);
		cameraTransform.rotate(vAngle, 1.0f, 0.0f, 0.0f);
		cameraTransform.translate(moveX, 0.0f, moveZ);
		
		//System.out.println("MouseDx=" + mouseDx);
		mouseDx = 0;
		mouseDy = 0;
	}

	/**
	 * Draw the current simulation state
	 * @param renderer Renderer
	 */
	public void draw(MyGLRenderer renderer)
	{
		// Calcul des FPS
		long time = System.currentTimeMillis();
		float elapsedTime = (float)(time-lastFrame);
		this.fpsTimer += elapsedTime;
		float frameTime = elapsedTime / 1000.0f;
		
		int elapsedSecond = 0;
		while(this.fpsTimer >= 1000) {
			this.fpsTimer -= 1000;
			elapsedSecond++;
		}
		
		if(elapsedSecond > 0) {
			float framePerSecond = frameCount / elapsedSecond;
			frameCount = 0;
			System.out.println("FPS=" + framePerSecond);
		}
		frameCount++;
		
		lastFrame = time;
		processEvents(frameTime);
	
		// Get shader to send uniform data
		TextureShader shaders=renderer.getShaders();
		// Get OpenGL context
		GL3 gl=renderer.getGL();
		
	
		rayCasting(gl);
		
		gl.glBindVertexArray(vaoId);
		shaders.setTexture(0);
		gl.glDrawArrays(GL3.GL_TRIANGLES, 0, 6);
				
		//MainActivity.log("Rendering terminated.");
	}
	
	
	protected void rayCasting(GL3 gl) 
	{
		
		final int w = PICTURE_WIDTH;
		final int h = PICTURE_HEIGHT;
		
		
		// On d�coupe en 16 blocs (4x4) 
		
		final int blockSizeX = (PICTURE_WIDTH+4-1) / 4;
		final int blockSizeY = (PICTURE_HEIGHT+4-1) / 4;
		
		List<RayCastingBlock> blocks = new ArrayList<>();
		
		for(int row=0; row < h; row+=blockSizeY) {
			for(int col=0; col < w; col+=blockSizeX) {
				blocks.add(new RayCastingBlock(col, row, blockSizeX, blockSizeY, w, h));
			}
		}
		
		try {
			executor.invokeAll(blocks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		gl.glBindTexture(GL3.GL_TEXTURE_2D, textureId);
		gl.glTexSubImage2D(GL3.GL_TEXTURE_2D, 0, 0, 0, w, h, GL3.GL_RGBA, GL3.GL_UNSIGNED_BYTE, bytePixelBuffer);
	}
	
	
	Vec3f findColor(Ray r, int order, double iRefractionFactor, double epsilon) {
		if(order == 0) 
			return new Vec3f(Color.BLACK);
		
		Shape hitShape = null;
		Shape.IntersectionResult hitResult = new Shape.IntersectionResult(null, Double.POSITIVE_INFINITY);

		
		for(Shape shape : shapes) {
			Shape.IntersectionResult result = shape.getIntersection(r, epsilon);
			if(result != null && result.rayLambda < hitResult.rayLambda) {
				hitResult = result;
				hitShape = shape;
			}
		}

		
		if(hitShape == null) {
			return new Vec3f(VOID_COLOR);
		}
		double lambda = hitResult.rayLambda;

		
		Vec3f hitPoint = r.getPoint((float)lambda);		
		Vec3f diffuseColor = new Vec3f(AMBIANT_COLOR);
		Vec3f specularColor = new Vec3f(Color.BLACK);
		Material material = hitShape.getMaterial();
		
		Vec3f finalDiffuse = material.getDiffuseColor();
		//float opacity = m
	
		if(material.hasDiffuseTexture()) {
			Vec2f tCoords = hitShape.getTextureCoordsAt(hitPoint);
			byte[] tColor = material.getDiffuseTexture().getColorAt(tCoords.x, tCoords.y);
			Vec3f tColor3f = new Vec3f((float)Byte.toUnsignedInt(tColor[0])/255.0f, (float)Byte.toUnsignedInt(tColor[1])/255.0f, (float)Byte.toUnsignedInt(tColor[2])/255.0f);			
			finalDiffuse.scale(tColor3f.x, tColor3f.y, tColor3f.z);
		}
		
		if(material.isIgnoreLight()) {
			return finalDiffuse;
		}
		
		Vec3f normal = hitShape.getNormalAt(hitPoint);

		for(Light light : ligths) {
			Vec3f hitPointToLight = light.getPos().sub(hitPoint);
			double lightDistance =  hitPointToLight.length();
			Ray lightRay = new Ray(hitPointToLight, hitPoint, lightDistance);
			
			
			
			if(!findIntersection(lightRay, epsilon)) {
				// On n'est pas � l'ombre de cette lumi�re
				Vec3f hitPointToLightNormalized = new Vec3f(hitPointToLight).normalize();
				double lightFactor = Math.abs(normal.dotProduct(hitPointToLightNormalized));
				Vec3f lightReflectDir = new Vec3f(normal).scale(2*normal.dotProduct(hitPointToLightNormalized)).sub(hitPointToLightNormalized).scale(-1.0f);
				
				double specularFactor = Math.max(0.0f, r.getDir().normalize().dotProduct(lightReflectDir));
				specularFactor = (float) Math.pow(specularFactor, material.getShininess());
				
				
				double attenuation = 1.0f/(LIGHT_C_ATT + LIGHT_L_ATT*lightDistance + LIGHT_Q_ATT*lightDistance*lightDistance);
				
				
				diffuseColor.add(light.getColor().scale(lightFactor*attenuation));
				specularColor.add(light.getColor().scale(specularFactor*attenuation));

			}else {
				//System.out.println("No light for you!");
			}
			
		}
		
		Vec3f reflectColor = new Vec3f(Color.BLACK);
		float specularFactor = material.getSpecularFactor();
		if(order > 1 && specularFactor > 0) {
			Vec3f reflectRayDir = new Vec3f(normal).scale(2.0f).add(r.getDir());
			Ray reflectRay = new Ray(reflectRayDir, hitPoint);
			reflectColor = findColor(reflectRay, order-1, iRefractionFactor, epsilon).scale(material.getSpecularFactor());
			//System.out.println(reflectColor);
		}
		
		
		diffuseColor.scale(material.getOpacity());
		// Refraction
		if(!material.isOpaque()) {

			Vec3f rayDir = r.getDir();
			double refractionFactor = iRefractionFactor / material.getRefractionFactor();
			double c1 = normal.dotProduct(rayDir);
			double c2 = Math.sqrt(1-(refractionFactor*refractionFactor)*(1-(c1*c1)));
			//System.out.println("c1=" + c1 + ",c2=" + c2 + ", test=" + (1-(refractionFactor*refractionFactor)*(1-(c1*c1))));

			Vec3f refractRayDir = rayDir.scale(refractionFactor).add(new Vec3f(normal).scale(refractionFactor*c1 - c2));

			Ray refractedRay = new Ray(refractRayDir, hitPoint);
			double newIncidentRefractFactor = 0;
			//System.out.println("c1=" + c1 + ", c2=" + c2);
			
			switch(hitResult.hitDir) {
				case INPUT:
					newIncidentRefractFactor = material.getRefractionFactor();
					break;
				case OUTPUT : 
					newIncidentRefractFactor = 1.0f;
					break;
				case PERPENDICULAR :
					newIncidentRefractFactor = iRefractionFactor;
					break;
				default : 
					System.out.println("error");
					break;
			}
			
			
			//System.out.println("c1=" + c1 + ", c2=" + c2);
			Vec3f refractedColor = findColor(refractedRay, order-1, newIncidentRefractFactor, epsilon);
			diffuseColor.addScale(1-material.getOpacity(), refractedColor);
		}
				
		finalDiffuse.scale(diffuseColor.x, diffuseColor.y, diffuseColor.z);
		Vec3f finalSpecular = material.getSpecularColor().scale(specularColor.x+reflectColor.x, specularColor.y+reflectColor.y, specularColor.z+reflectColor.z).scale(material.getOpacity());
		return finalDiffuse.add(finalSpecular);
	}
	
	boolean findIntersection(Ray r, double epsilon) {
		for(Shape shape : shapes) {
			IntersectionResult intersection = shape.getIntersection(r, epsilon);
			if(intersection != null && intersection.rayLambda <= r.getLength())
				return true;
		}
		
		return false;
	}


	@Override public void mouseDragged(MouseEvent e) {}


	@Override
	public void mouseMoved(MouseEvent e) {
		
		if(pressMap.get(KeyEvent.VK_CONTROL)) {
			mouseDx += e.getX() - mouseLastX;
			mouseDy += e.getY() - mouseLastY;		
		}else {
			mouseDx = 0;
			mouseDy = 0;
		}
		
		mouseLastX = e.getX();
		mouseLastY = e.getY();			
	}

	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mousePressed(MouseEvent e) { }
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {	}
	@Override public void mouseExited(MouseEvent e) {}
	@Override public void keyTyped(KeyEvent e) { }


	@Override
	public void keyPressed(KeyEvent e) {
		
		int keyCode = e.getKeyCode();
		if(pressMap.get(keyCode) == true) {
			return;
		}
		pressMap.put(keyCode, true);

		
		switch (keyCode){
			case KeyEvent.VK_LEFT: 
				camDirX -= 1;
				break;
			case KeyEvent.VK_RIGHT:
				camDirX += 1;
				break;
			case KeyEvent.VK_UP:
				camDirZ -= 1;
				break;
			case KeyEvent.VK_DOWN:
				camDirZ += 1;
				break;	
			default:
				break;
		}
	}


	@Override
	public void keyReleased(KeyEvent e) {
		
		int keyCode = e.getKeyCode();
		pressMap.put(keyCode, false);
		
		switch (keyCode){
		case KeyEvent.VK_LEFT: 
			camDirX += 1;
			break;
		case KeyEvent.VK_RIGHT:
			camDirX -= 1;
			break;
		case KeyEvent.VK_UP:
			camDirZ += 1;
			break;
		case KeyEvent.VK_DOWN:
			camDirZ -= 1;
			break;	
		default:
			break;
		}
	}


	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		mouseWheelRot += e.getWheelRotation();
	}
	
	
	
	private class RayCastingBlock implements Callable<Void> {
		
		int colOffset;
		int rowOffset;
		int colCount;
		int rowCount;
		int w, h;
		
		public RayCastingBlock(int colOffset, int rowOffset, int colCount, int rowCount, int w, int h){
			this.colOffset =colOffset;
			this.colCount =colCount;
			this.rowCount = rowCount;
			this.rowOffset = rowOffset;
			this.w = w;
			this.h = h;
		}
		

		@Override
		public Void call() throws Exception {
						
			float[] transformedRayDir = new float[4];
	        float[] cameraPos = {0.0f, 0.0f, 0.0f, 1.0f};
	        float[] transformedCameraPos = new float[4];
	        cameraTransform.multVec(cameraPos, transformedCameraPos);
	        		            
			for(int row = rowOffset; (row < rowOffset+rowCount) && (row < h); row++){ // for each row of the image
				for(int col = colOffset; (col < colOffset+colCount) && (col < w); col++){ // for each column of the image
		            
		            int index = 4*((row*w)+col); // compute index of color for pixel (x,y) in the buffer
		            
		            // Ensure that the pixel is black
		            //pixelBuffer[index]=0; // blue : take care, blue is the first component !!!
		            //pixelBuffer[index+1]=0; // green
		            //pixelBuffer[index+2]=0; // red (red is the last component !!!)
		            pixelBuffer[index+3]=(byte)255;
		            
		            // Depending on the x position, select a color... 
		            float D = projectionPlaneDistance;
		            float x = (float)(col-w/2)/(float)h;
		            float y = (float)(row-h/2)/(float)h;
		            float z = -D;
		            
		 		   float[] rayDir = {x, y, z, 0.0f};
		            cameraTransform.multVec(rayDir, transformedRayDir);
		                            
		            Ray ray = new Ray(new Vec3f(transformedRayDir[0], transformedRayDir[1], transformedRayDir[2]), new Vec3f(transformedCameraPos[0], transformedCameraPos[1], transformedCameraPos[2]));
		            
		  
		            Vec3f color = findColor(ray, RAYCAST_ORDER, 1.0f, DEFAULT_EPSILON);
		            
		            byte r = (byte) Math.min(255, Math.round(color.x * 255.0f));
		            byte g = (byte) Math.min(255,Math.round(color.y * 255.0f));
		            byte b = (byte) Math.min(255,Math.round(color.z * 255.0f));
		
		            pixelBuffer[index] = r;
		            pixelBuffer[index+1] = g;
		            pixelBuffer[index+2] = b;
		
		            //if (col<w/3) buffer[index]=(byte)255; // Blue in the left part of the image
		            //else if (col<2*w/3) buffer[index+1]=(byte)255; // Green in the middle
		            //else buffer[index+2]=(byte)255; // Red in the right part
				}
			}
			return null;
		}
		
	}
	
}

