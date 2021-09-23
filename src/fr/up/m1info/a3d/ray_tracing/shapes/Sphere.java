package fr.up.m1info.a3d.ray_tracing.shapes;

import fr.up.m1info.a3d.ray_tracing.Material;
import fr.up.m1info.a3d.ray_tracing.Ray;
import fr.up.m1info.a3d.ray_tracing.Vec2f;
import fr.up.m1info.a3d.ray_tracing.Vec3f;

public class Sphere extends Shape {

	private Vec3f center;
	private double radius;
	
	
	public Sphere(Material material) {
		super(material);
		this.center = new Vec3f(0.0f, 0.0f, 0.0f);
		this.radius = 1.0f;
	}
	
	public Sphere(Vec3f center, double radius, Material material) {
		super(material);
		this.center = new Vec3f(center);
		this.radius = radius;
	}
	
	@Override
	public IntersectionResult getIntersection(Ray ray, double epsilon) {
			
		Vec3f centerToRayStart = ray.getStart().sub(this.center);
		
		double a = 1;
		double b = ray.getDir().scale(2.0f).dotProduct(centerToRayStart);
		double c = Math.pow(centerToRayStart.length(), 2.0) - (radius*radius);
		
		double delta = b*b - 4*a*c;
		
		if(delta < 0)
			return null;
		
		if(delta == 0)
			return new IntersectionResult(HitDirection.PERPENDICULAR, -b/(2*a));
		
		
		double lambda1 = (-b - Math.sqrt(delta)) / (2*a);
		double lambda2 = (-b + Math.sqrt(delta)) / (2*a);
		
		if(lambda2 > epsilon) {
			
			if(lambda1 > epsilon) {
				return new IntersectionResult(HitDirection.INPUT, lambda1);
			}
			
			
			// On se situe à l'intérieur de la sphère, on en sort
			return new IntersectionResult(HitDirection.OUTPUT, lambda2);
		}
		
		return null;
	}
	
	@Override
	public Vec3f getNormalAt(Vec3f point) {
		return new Vec3f(point).sub(center).normalize();
	}

	@Override
	public Vec2f getTextureCoordsAt(Vec3f point) {
		return new Vec2f(0.0f, 0.0f);
	}
	
	
}
