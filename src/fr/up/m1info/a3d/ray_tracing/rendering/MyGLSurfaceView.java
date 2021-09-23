package fr.up.m1info.a3d.ray_tracing.rendering;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * Class to described the surface view
 * @author Hakim Belhaouari and Philippe Meseure
 */
public class MyGLSurfaceView extends JPanel
	implements KeyListener,MouseListener, MouseMotionListener
{	
	/**
	 * Mandatory for Serialization...
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Framerate for animation. At least 25 for PAL, can be higher...
	 */
	static private final int FRAMERATE=25;
	private GLCanvas canvas; // JOGL canvas
	private FPSAnimator fpsanimator;
	/**
	 * Renderer, bridge to opengl area and opengl context
	 */
	private MyGLRenderer renderer;
	/**
	 * Reference to the global scene
	 */
	private Scene scene;

	/**
	 * @return Renderer used for the surface view
	 */
	public MyGLRenderer getRenderer()
	{
		return renderer;
	}
	
	/**
	 * @return Scene containing virtual objects
	 */
	public Scene getScene()
	{
		return scene;
	}
	
	/**
	 * Main constructor
	 * @param scene Global graphics scene that includes all 3D objects
 */
	public MyGLSurfaceView(Scene scene)
	{
		this.scene = scene;
		renderer = new MyGLRenderer(this, scene);
				
		final GLProfile glprofile = GLProfile.getGL2GL3();
		final GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		glcapabilities.setDoubleBuffered(true);

		MainActivity.log("GL Profile: " + glprofile.getName());
		MainActivity.log("GL Profile (concrete): " + glprofile.getImplName());
		if(glprofile.hasGLSL())
		{
			MainActivity.log("GLSL present! yeah!");
		}
		
		canvas = new GLCanvas(glcapabilities);
		canvas.addGLEventListener(renderer);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		
		canvas.addKeyListener(scene);
		canvas.addMouseListener(scene);
		canvas.addMouseMotionListener(scene);
		canvas.addMouseWheelListener(scene);

		
		fpsanimator = new FPSAnimator(canvas, FRAMERATE, true);

		setLayout(new BorderLayout(0, 0));
		add(canvas, BorderLayout.CENTER);
		canvas.requestFocus();
	}
	

	public boolean start()
	{
		return fpsanimator.start();
	}
	
	public void stop()
	{
		fpsanimator.stop();
	}

	
	// Evenement pour les touches claviers
	@Override
	public void keyTyped(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_ESCAPE:
				MainActivity.log("Bye!");
				System.exit(0);
				break;
			default:
				switch (e.getKeyChar()) {
					case 'q':
						MainActivity.log("Bye!");
						System.exit(0);
						break;
					default:
						break;
				}
				break;
		}
		
		
	}
	

	@Override
	public void keyPressed(KeyEvent e)
	{
		// TODO Auto-generated method stub	
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub	
	}
	
	public void refresh()
	{
		canvas.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub	
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		lastMousePress = e.getPoint();	
	}

	private Point lastMousePress;
	
	@Override
	public void mouseReleased(MouseEvent e)
	{		
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		int deltax = e.getX() - lastMousePress.x;
		int deltay = e.getY() - lastMousePress.y;
		
		// to complete
		// You can use deltax and deltay to make mouse motion control the position
		// and/or the orientation of the viewer
		
		lastMousePress = e.getPoint();
	}

	
	
	@Override
	public void mouseMoved(MouseEvent e)
	{	
	}
}
