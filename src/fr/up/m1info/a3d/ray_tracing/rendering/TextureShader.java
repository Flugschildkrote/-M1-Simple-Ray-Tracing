package fr.up.m1info.a3d.ray_tracing.rendering;

import com.jogamp.opengl.GL3;

public class TextureShader extends BasicShaders {

	
	private final int aTCoordsPosition = 1;
	
	private int uTextureLocation;
	
	
	public TextureShader(MyGLRenderer renderer) {
		super(renderer);
	}

	@Override
	public int createProgram() {
		return initializeShadersFromResources(this.gl, "res/texture.vert", "res/texture.frag");
	}
	
	
	@Override
	public void findVariables() {
		uTextureLocation = gl.glGetUniformLocation(shaderprogram, "uTexture");
	}
	
	
	public void setTexture(int textureUnit) {
		gl.glUniform1i(uTextureLocation, textureUnit);
	}
	
	public void setTCoordsPointer(final int size,final int dtype) {
		gl.glVertexAttribPointer(this.aTCoordsPosition,size,dtype,false,0,0);
	}

}
