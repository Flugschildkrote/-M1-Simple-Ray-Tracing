package fr.up.m1info.a3d.ray_tracing;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.security.InvalidParameterException;

import javax.imageio.ImageIO;


public class Texture {

	byte[] pixels;
	int components, width, height;

	
	public Texture(String path) throws IOException {
		this(ImageIO.read(Texture.class.getClassLoader().getResourceAsStream(path)));
	}
	
	public Texture(BufferedImage picture) {
		if(picture == null)
			throw new InvalidParameterException("Failed to create texture with null picture!");
		
		
		pixels = ((DataBufferByte) picture.getRaster().getDataBuffer()).getData();
		this.width = picture.getWidth();
		this.height = picture.getHeight();
		this.components = picture.getAlphaRaster() == null ? 3 : 4;
		
	}
	
	
	
	public byte[] getColorAt(double u, double v) {
		int xCoord = (int) Math.round((double) (width-1)*u);
		int yCoord = (int) Math.round((double) (height-1)*v);

		int index = width*yCoord*components + xCoord*components;
		return new byte[] { 
				pixels[index+2], 
				pixels[index+1], 
				pixels[index],  
				(components==4 ? pixels[index+3] : (byte)255) 
		};
		
	}
	
	
	
}
