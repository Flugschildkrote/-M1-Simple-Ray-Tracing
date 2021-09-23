package fr.up.m1info.a3d.ray_tracing.shapes;

import fr.up.m1info.a3d.ray_tracing.Material;
import fr.up.m1info.a3d.ray_tracing.Ray;
import fr.up.m1info.a3d.ray_tracing.Vec2f;
import fr.up.m1info.a3d.ray_tracing.Vec3f;

public class Triangle extends Plane {

	private Vec3f p1, p2, p3;
	private Vec2f p1UV, p2UV, p3UV;
	private double area;
	
	
	public Triangle(Vec3f p1, Vec3f p2, Vec3f p3, Material material) {
		super(material);
		
		Vec3f p1ToP2 = new Vec3f(p2).sub(p1);
		Vec3f p1ToP3 = new Vec3f(p3).sub(p1);

		this.normal.setCrossProduct(p1ToP2, p1ToP3);
		this.area = normal.length() / 2;
		this.normal.normalize();
		this.d = -this.normal.x*p1.x -this.normal.y*p1.y - this.normal.z*p1.z;
		
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		
		this.p1UV = new Vec2f(0.0f, 0.0f);
		this.p2UV = new Vec2f(1.0f, 0.0f);
		this.p3UV = new Vec2f(1.0f, 1.0f);
	}
	
	
	@Override
	public IntersectionResult getIntersection(Ray ray, double epsilon) {
		IntersectionResult result = super.getIntersection(ray, epsilon);
		
		if(result == null)
			return result;
		
		Vec3f intersection = ray.getStart().add(ray.getDir().scale((double)result.rayLambda));
		
		Vec3f I_P1 = new Vec3f(intersection).sub(p1);
		Vec3f I_P2 = new Vec3f(intersection).sub(p2);
		Vec3f I_P3 = new Vec3f(intersection).sub(p3);

		double a = normal.dotProduct(new Vec3f().setCrossProduct(I_P1, I_P2));		
		double b = normal.dotProduct(new Vec3f().setCrossProduct(I_P2, I_P3));		
		double c = normal.dotProduct(new Vec3f().setCrossProduct(I_P3, I_P1));	
		

		if(a >= 0 && b >= 0 && c >= 0)
			return result;
		
		// Not inside the triangle
		return null;
	}
	
	@Override
	public Vec2f getTextureCoordsAt(Vec3f point) {
		
		// Calcul des uvs avec les coordonées barycentriques 
		
		double alpha = 0.0; // area ratio of triangle 12I
		double beta = 0.0; // area ratio of triangle  23I
		double gamma = 0.0; // area ratio of triangle 31I
		
		Vec3f p1ToP2 = new Vec3f(p2).sub(p1);
		Vec3f p1ToPoint = new Vec3f(point).sub(p1);

		Vec3f p2ToP3 = new Vec3f(p3).sub(p2);
		Vec3f p2ToPoint = new Vec3f(point).sub(p2);
		
		Vec3f p3ToP1 = new Vec3f(p1).sub(p3);
		Vec3f p3ToPoint = new Vec3f(point).sub(p3);
		
		double alphaArea = (new Vec3f().setCrossProduct(p1ToP2, p1ToPoint).length())/2;
		double betaArea = (new Vec3f().setCrossProduct(p2ToP3, p2ToPoint).length())/2;
		double gammaArea = (new Vec3f().setCrossProduct(p3ToP1, p3ToPoint).length())/2;

		alpha = alphaArea/area;
		beta = betaArea/area;
		gamma = gammaArea/area;

		
		Vec2f tCoords = new Vec2f(0.0f, 0.0f);
		tCoords.addScale(beta, p1UV);
		tCoords.addScale(gamma, p2UV);
		tCoords.addScale(alpha, p3UV);

		tCoords.x = (tCoords.x < 0) ? 1-(-tCoords.x % 1.0) : (tCoords.x % 1.0);
		tCoords.y = (tCoords.y < 0) ? 1-(-tCoords.y % 1.0) : (tCoords.y % 1.0);

		
		return tCoords;
		
	}

}
