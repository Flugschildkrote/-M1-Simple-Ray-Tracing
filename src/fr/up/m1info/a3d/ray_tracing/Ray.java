package fr.up.m1info.a3d.ray_tracing;

public class Ray {

	private Vec3f dir;
	private Vec3f start;
	private double length;
	
	public Ray(Vec3f dir, Vec3f start) {
		this(dir, start, Double.POSITIVE_INFINITY);
	}
	
	public Ray(Vec3f dir, Vec3f start, double length) {
		this.dir = new Vec3f(dir).normalize();
		this.start = new Vec3f(start);
		this.length = length;
	}
	
	public Vec3f getDir() {
		return new Vec3f(this.dir);
	}
	
	public Vec3f getStart() {
		return new Vec3f(this.start);
	}
	
	
	public Vec3f getPoint(double lambda) {
		return new Vec3f(this.start).addScale(lambda, dir);
	}
	
	@Override
	public String toString() {
		return "Ray{dir=" + this.dir + ", start=" + this.start + ", length=" + this.length + "}";
	}
	
	public double getLength() {
		return this.length;
	}
}
