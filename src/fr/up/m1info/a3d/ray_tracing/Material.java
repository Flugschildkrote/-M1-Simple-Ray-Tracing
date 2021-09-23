package fr.up.m1info.a3d.ray_tracing;

public class Material {

    //public static int DIFFUSE_TEXTURE_INDEX = 0;
    //public static int REFLECT_MAP_INDEX = 1;

    //public boolean lightingEnabled;
    //public int diffuseTexture;

    //public int reflectMap;
	private Texture diffuseTexture;
	private Vec3f diffuseColor;
    private Vec3f specularColor;
    private float shininess;
    private float opacity;
    private float specularFactor;
    private float refractionFactor;
    private boolean ignoreLight;

    public Material(){
        //this.lightingEnabled = true;
        this.diffuseTexture = null;
        //this.reflectMap = 0;
        this.diffuseColor = new Vec3f(1.0f, 1.0f, 1.0f);
        this.specularColor = new Vec3f(1.0f, 1.0f, 1.0f);
        this.shininess = 1.0f;
        this.opacity = 1.0f;
        this.specularFactor = 0.0f;
        this.refractionFactor = 1.0f;
        this.ignoreLight = false;
    }

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public Vec3f getSpecularColor() {
        return new Vec3f(specularColor);
    }

    public void setSpecularColor(Vec3f specularColor) {
        this.specularColor = specularColor;
    }

    public Vec3f getDiffuseColor() {
        return new Vec3f(diffuseColor);
    }

    public void setDiffuseColor(Vec3f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }
    
    public void setSpecularFactor(float sFactor) {
    	this.specularFactor = sFactor;
    }
    
    public float getSpecularFactor() {
    	return this.specularFactor;
    }

    public boolean hasDiffuseTexture(){ return this.diffuseTexture != null; }

  //  public boolean hasReflectMap(){ return this.reflectMap != 0; }

    public Texture getDiffuseTexture() {
        return diffuseTexture;
    }

    public void setDiffuseTexture(Texture diffuseTexture) {
        this.diffuseTexture = diffuseTexture;
    }

    
    /*public boolean isLightingEnabled() {
        return lightingEnabled;
    }

    public void setLightingEnabled(boolean lightingEnabled) {
        this.lightingEnabled = lightingEnabled;
    }*/

    public float getRefractionFactor() {
		return refractionFactor;
	}

	public void setRefractionFactor(float refractionFactor) {
		this.refractionFactor = refractionFactor;
	}

	public float getOpacity() {
        return this.opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public boolean isOpaque(){
        return this.opacity == 1.0f;
    }

	public boolean isIgnoreLight() {
		return ignoreLight;
	}

	public void setIgnoreLight(boolean ignoreLight) {
		this.ignoreLight = ignoreLight;
	}
    
    

   // public int getReflectMap() { return reflectMap; }

   // public void setReflectMap(int reflectMap) { this.reflectMap = reflectMap; }
}
