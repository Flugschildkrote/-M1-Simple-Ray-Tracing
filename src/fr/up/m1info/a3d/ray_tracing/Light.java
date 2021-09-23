package fr.up.m1info.a3d.ray_tracing;

public class Light {

	private Vec3f color;
	private Vec3f pos;

	public Light() {
		this.color = Color.WHITE;
		this.pos = new Vec3f(0.0f, 0.0f, 0.0f);
	}
	
	public Light(Vec3f color, Vec3f pos) {
		this.color = new Vec3f(color);
		this.pos = new Vec3f(pos);
	}
	
	public Vec3f getColor() {
		return new Vec3f(color);
	}
	
	public void setColor(Vec3f color) {
		this.color = color;
	}
	
	public Vec3f getPos() {
		return new Vec3f(pos);
	}
	
	public void setPos(Vec3f pos) {
		this.pos = pos;
	}
	
}
