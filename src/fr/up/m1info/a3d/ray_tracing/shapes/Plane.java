package fr.up.m1info.a3d.ray_tracing.shapes;

import fr.up.m1info.a3d.ray_tracing.Material;
import fr.up.m1info.a3d.ray_tracing.Ray;
import fr.up.m1info.a3d.ray_tracing.Vec2f;
import fr.up.m1info.a3d.ray_tracing.Vec3f;

public class Plane extends Shape {
	
	private final static Vec3f S_DEFAULT_NORMAL = new Vec3f(0, 1, 0);
	private final static double S_DEFAULT_D = 0;
	
	
	protected Vec3f normal;
	protected double d;
	
	private Vec3f uvXAxis, uvYAxis, point;

	
	protected Plane(Material material) {
		super(material);
		this.normal = S_DEFAULT_NORMAL;
		this.d = S_DEFAULT_D;
	}
	
	
	/**
	 * 
	 * @param normal The normal of the plane
	 * @param point A point of the plane. The texturing will start at (0, 0) at this point
	 * @param uvXAxis Direction of the texture x Axis. Change the length to change the tile size.
	 * @param uvYAxis Direction od the texture y Axis. Change the length to change the tile size.
	 * @param material The material to use for drawing
	 */
	public Plane(Vec3f normal, Vec3f point, Vec3f uvXAxis, Vec3f uvYAxis, Material material) {
		super(material);
		
		this.normal = new Vec3f(normal).normalize();
		this.d = -this.normal.x*point.x -this.normal.y*point.y - this.normal.z*point.z;
		this.uvXAxis = new Vec3f(uvXAxis);
		this.uvYAxis = new Vec3f(uvYAxis);
		this.point = new Vec3f(point);
	}
	

	
	

	@Override
	public IntersectionResult getIntersection(Ray ray, double epsilon) {
		
		Vec3f rayStart = ray.getStart();
		Vec3f rayDir = ray.getDir();
		
		double n = (-normal.x*rayStart.x - normal.y*rayStart.y - normal.z*rayStart.z - d);
		double div = (normal.x*rayDir.x + normal.y*rayDir.y + normal.z*rayDir.z);
		
		// Si rayon parall�le, pas d'intersection
		if(div == 0)
			return null;

		double result = n/div;
		if(result <= epsilon)
			return null;
		
		HitDirection dir = rayDir.dotProduct(normal) > 0 ? HitDirection.OUTPUT : HitDirection.INPUT;
		return new IntersectionResult(dir, result);
	}
	
	@Override
	public Vec3f getNormalAt(Vec3f point) {
		return new Vec3f(normal);
	}

	
	@Override
	public String toString() {
		return "Plane{a=" + normal.x + ", b=" + normal.y + ", c=" + normal.z + ", d=" + d + "}";
	}


	@Override
	public Vec2f getTextureCoordsAt(Vec3f lPoint) {
		
		Vec3f auxPoint = new Vec3f(lPoint).sub(this.point);
		
		double x = this.uvXAxis.dotProduct(auxPoint);
		double y = this.uvYAxis.dotProduct(auxPoint);
		
		x = x < 0 ? 1-((-x)%1) : (x % 1);
		y = y < 0 ? 1-((-y)%1) : (y % 1);
		
		return new Vec2f(x, y);
	}
	
	/*public void setTiling(Vec2f tiling) {
		this.tiling.set(tiling);
	}*/
}
