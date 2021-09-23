package fr.up.m1info.a3d.ray_tracing.rendering;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.JFrame;


/**
 * Main class
 * @author Hakim Belhaouari & Philippe Meseure
 */
public class MainActivity
{
	private static Logger logger = Logger.getGlobal();
	private static final int WIDTH=1024;
	private static final int HEIGHT=768;
	
	public static void log(String message)
	{
		logger.info(message);
	}
	
	public static void main(String[] args) throws IOException
	{
		log(String.format("Logging processus for info level...."));
		
		JFrame frame = new JFrame("Algprithmique 3D");
		frame.addKeyListener( new KeyAdapter() {} );
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(WIDTH,HEIGHT));

		Scene scene = new Scene();
		MyGLSurfaceView view = new MyGLSurfaceView(scene);
		
		frame.getContentPane().add(view);
		frame.setSize(WIDTH,HEIGHT);
		frame.pack();
		
		logger.info("Fin set visible");
		view.start();
		
		frame.setVisible(true);
	}
}
