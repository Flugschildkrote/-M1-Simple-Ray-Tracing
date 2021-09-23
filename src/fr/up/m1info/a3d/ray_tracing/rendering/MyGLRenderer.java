package fr.up.m1info.a3d.ray_tracing.rendering;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.InputStream;


/**
 * Class to represent the rendering of the scene.
 * It is a kind of bridge toward OpenGL
 * @author Hakim Belhaouari & Philippe Meseure
 */
public class MyGLRenderer implements GLEventListener
{	
	static public final float[] black={0.F,0.F,0.F,1.F};
	static public final float[] darkgray = { 0.2f,0.2f,0.2f };
	static public final float[] gray = { 0.5f,0.5f,0.5f };
	static public final float[] lightgray = { 0.8f,0.8f,0.8f };
	static public final float[] white={1.F,1.F,1.F,1.F};
	static public final float[] red={1.F,0.F,0.F,1.F};
	static public final float[] green={0.F,1.F,0.F,1.F};
	static public final float[] blue={0.F,0.F,1.F,1.F};
	static public final float[] yellow={1.F,1.F,0.F,0.5F};
	static public final float[] magenta={1.F,0.F,1.F,1.F};
	static public final float[] cyan={0.F,1.F,1.F,1.F};
	static public final float[] orange={1.F,0.5F,0.F,0.5F};

	/**
	 * Reference to the scene environment
	 */
	private final Scene scene;

	/**
	 * Reference to the OpenGL surface view
	 */
	private final MyGLSurfaceView view;

	/**
	 * Projection matrix to provide to the shader
	 */
	private final float[] projectionmatrix = new float[16];
		
	/*
	 * OpenGL context
	 */
	private GLAutoDrawable glarea;

	/**
	 * shaders
	 */
	private TextureShader shaders;
	
	// ********************** GETTERS *********************
	
	/**
		* @return the scene environment
		*/
  public Scene getScene() { return this.scene; }

	/**
	 * @return OpenGL ES context
		 */
	public GL3 getGL() { return glarea.getGL().getGL3(); }

	/**
	 * @return the current GL Surface View
	 */
	public MyGLSurfaceView getView() { return this.view; }

	/**
	 * @return fragment and vertex shaders
	 */
	public TextureShader getShaders()
	{
		return shaders;
	}
   
	// ******************** METHODS **********************
	
	/**
	 * Constructor
	 * @param view OpenGL surface view
	 * @param scene the scene environment
	 */        
  public MyGLRenderer(MyGLSurfaceView view, Scene scene)
	{
		this.scene = scene;
		this.view = view;
		shaders = null;
	}
	

	/**
	 * Method called regularly. It Modifies the scene (in case of animation)
	 * and draws it.
	 * @param gl : OpenGL zone
	 */
	@Override
	public void display(GLAutoDrawable gl)
	{
		this.scene.step();
		gl.getGL().glClear(GL.GL_COLOR_BUFFER_BIT|GL.GL_DEPTH_BUFFER_BIT);
		this.scene.draw(this);		
		gl.getGL().glFlush();		
		// view.refresh();
	}


	/**
	 * Function called when window is closed...
	 */
	@Override
	public void dispose(GLAutoDrawable arg0)
	{
		// TODO Auto-generated method stub

		// memory should be cleaned here...
	}


	/**
	 * Called when the rendering surface is created
	 * @param arg0 OpenGL area
	 */
	@Override
	public void init(GLAutoDrawable arg0)
	{
		// onSurfaceCreated
		this.glarea = arg0;
		try
		{
			shaders = new TextureShader(this);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(324);
		}
		checkGlError(arg0, "Shaders Creation");
		scene.initGraphics(this);
		getGL().glUseProgram(shaders.shaderprogram);
	}

	/**
	 * Function when the current window is reshaped
	 * @param arg0 OpenGL area
	 * @param x
	 * @param y
	 * @param w
	 * @param h 
	 */
	@Override
	public void reshape(GLAutoDrawable arg0, int x, int y, int w, int h)
	{
		// onSurfaceChanged
		arg0.getGL().glViewport(x, y, w, h);
		float ratio = (float)w / (float)h;
		
		if(w > h)
		{
			FloatUtil.makePerspective(projectionmatrix,0,true, FloatUtil.QUARTER_PI,ratio,0.1f,100.f);
		}
		else
		{
			FloatUtil.makePerspective(projectionmatrix,0,true,FloatUtil.QUARTER_PI, ratio,0.1f,100.f);
		}
		MainActivity.log(String.format("Reshape: x=%d\ty=%d\twidth=%d\theight=%d", x,y,w,h));
		
		StringBuilder sb = new StringBuilder();
		for(int i=0;i < 4;i++)
		{
			for(int j= 0;j < 4;j++)
			{
				sb.append(String.format(" % 3.3f",projectionmatrix[(i*4)+j]));
			}
			sb.append("\n");
		}
		MainActivity.log("Projection: \n" +sb.toString());
	}
	
	/**
	 * Function to call in order to analyse OpenGL errors
	 * @param glad : OpenGL area
	 * @param gloperation : String to describe last OpenGL call
	 */
	public static void checkGlError(GLAutoDrawable glad, String gloperation)
	{
		GL3 gl = glad.getGL().getGL3();
		checkGlError(gl,gloperation);
  }

	/**
	 * Function to call in order to analyse OpenGL errors
	 * @param gl OpenGL context
	 * @param gloperation String to describe last OpenGL call
	 */	
	public static void checkGlError(GL3 gl, String gloperation)
	{
		int firsterror,error;

		// Check if there is an error
		error = gl.glGetError();
		if (error== GL.GL_NO_ERROR) return;

		// In case of error, display the error list and throw an exception...
		firsterror=error;
		do
		{
			MainActivity.log("Gl Error "+error+" after "+gloperation);
			error = gl.glGetError();
		} while (error!=GL.GL_NO_ERROR);
		throw new RuntimeException("GL Error "+firsterror+" after "+gloperation);
	}
	
	/**
	 * Utility method to load a texture defined as a resource
	 * @return Texture handle
	 */
	public static Texture loadTexture(GL3 gl,String filename)
	{		
		Texture texture=null;
		try {
			ClassLoader classloader=MyGLRenderer.class.getClassLoader();
			InputStream input=classloader.getResourceAsStream(filename);

			texture=TextureIO.newTexture(input, false,TextureIO.JPG);
		}
		catch(Exception e)
		{
			MainActivity.log("Error with texture file "+filename+":"+e);
			return null;
		}
		
		if (texture!=null)
		{
			// enable texture
			texture.enable(gl);
			// Set filtering
			texture.setTexParameteri(gl, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
			texture.setTexParameteri(gl, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
		}
		return texture;
	}
}
