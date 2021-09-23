package fr.up.m1info.a3d.ray_tracing.shapes;

import fr.up.m1info.a3d.ray_tracing.Material;
import fr.up.m1info.a3d.ray_tracing.Ray;
import fr.up.m1info.a3d.ray_tracing.Vec2f;
import fr.up.m1info.a3d.ray_tracing.Vec3f;

public abstract class Shape {

	private Material material;
	
	public Shape(Material material) {
		this.material = material;
	}
	
	
	public abstract IntersectionResult getIntersection(Ray r, double epsilon);
	public abstract Vec3f getNormalAt(Vec3f point);
	public abstract Vec2f getTextureCoordsAt(Vec3f point);
	
	
	
	
	public Material getMaterial() {
		return this.material;
	}
	
	
	public static class IntersectionResult{
		
		public IntersectionResult(HitDirection dir, double lambda) {
			this.hitDir = dir;
			this.rayLambda = lambda;
		}
		
		public HitDirection hitDir;
		public double rayLambda;
	}
	
	
	public static enum HitDirection{
		INPUT,
		OUTPUT,
		PERPENDICULAR
	}
	
}
