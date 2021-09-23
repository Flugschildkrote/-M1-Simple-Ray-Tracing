package fr.up.m1info.a3d.ray_tracing;

/**
 * Basic class to represent 3-vectors
 * Not intended to be a complete algebra class !
 * @author Philippe Meseure
 * @version 1.0
 */
public class Vec2f
{
    /**
     * x and y values of the current vector.
     * These are public to allow fast access and simple use.
     */
    public double x,y;

    /**
     * Default Constructor
     */
    public Vec2f()
    {
        this.x=this.y=0.F;
    }

    /**
     * Constructor with initialisation
     * @param x
     * @param y
     */
    public Vec2f(final double x,final double y)
    {
        this.x=x;
        this.y=y;
    }

    /**
     * Constructor by copy
     * @param that vector to be copied in current vector
     */
    public Vec2f(final Vec2f that)
    {
        this.x=that.x;
        this.y=that.y;
    }

    /**
     * Set current vector's value to 0.0
     * @return current vector
     */
    public Vec2f reset()
    {
        this.x=this.y=0.F;
        return this;
    }

    /**
     * Copy "that" vector in current vector
     * @param that vector to be copied
     * @return current vector
     */
    public Vec2f set(final Vec2f that)
    {
        this.x=that.x;
        this.y=that.y;
        return this;
    }

    /**
     * Copy x, y and z in current vector
     * @param x,y,z values to place into current vector
     * @return current vector
     */
    public Vec2f set(final double x,final double y)
    {
        this.x=x;
        this.y=y;
        return this;
    }

    /**
     * @return square of the length of current vector
     */
    public double lengthSquare()
    {
        return this.x*this.x+this.y*this.y;
    }
    /**
     * @return length of current vector
     */
    public double length()
    {
        return (double)Math.sqrt(this.x*this.x+this.y*this.y);
    }

    /**
     * Normalize current vector
     * @return current vector
     */
    public Vec2f normalize()
    {
        double l=this.lengthSquare();
        if (l==0.F) return this;
        l=(double)Math.sqrt(l);
        return this.scale(1.F/l);
    }

    /**
     * Add a vector to current vector
     * @param that any vector
     * @return current vector
     */
    public Vec2f add(final Vec2f that)
    {
        this.x+=that.x;
        this.y+=that.y;
        return this;
    }

    /**
     * Add two vectors v1 and v2 and put result into current vector
     * @param v1 any vector
     * @param v2 any vector
     * @return current vector
     */
    public Vec2f setAdd(final Vec2f v1, final Vec2f v2)
    {
        this.x=v1.x+v2.x;
        this.y=v1.y+v2.y;
        return this;
    }

    /**
     * Subtract a vector to current vector
     * @param that vector to subtract
     * @return current vector
     */
    public Vec2f sub(final Vec2f that)
    {
        this.x-=that.x;
        this.y-=that.y;
        return this;
    }

    /**
     * Subtract two vectors and put result into current vector
     * @param v1 any vector
     * @param v2 any vector
     * @return
     */
    public Vec2f setSub(final Vec2f v1,final Vec2f v2)
    {
        this.x=v1.x-v2.x;
        this.y=v1.y-v2.y;
        return this;
    }

    /**
     * Scale current vector uniformly
     * @param scale uniform scale factor
     * @return current vector
     */
    public Vec2f scale(final double scale)
    {
        this.x*=scale;
        this.y*=scale;
        return this;
    }

    /**
     * Scale current vector with specific factors for each coordinate
     * @param scalex scale factor for x
     * @param scaley scale factor for y
     * @return current vector
     */
    public Vec2f scale(final double scalex,final double scaley,final double scalez)
    {
        this.x*=scalex;
        this.y*=scaley;
        return this;
    }

    /**
     * Scale a given vector by a uniform scale and put result into current vector
     * @param scale scale factor
     * @param that vector to scale
     * @return current vector
     */
    public Vec2f setScale(final double scale,final Vec2f that)
    {
        this.x=scale*that.x;
        this.y=scale*that.y;
        return this;
    }

    /**
     * Scale a given vector by factors provided in another vector and put result into current vector
     * @param v1 vector to scale
     * @param v2 scale factors for x, y and z
     * @return current vector
     */
    public Vec2f setScale(final Vec2f v1,final Vec2f v2)
    {
        this.x=v1.x*v2.x;
        this.y=v1.y*v2.y;
        return this;
    }

    /**
     * Add a given vector that is before-hand scaled, to the current vector
     * @param scale scale factor
     * @param that vector to scale and add to current vector
     * @return current vector
     */
    public Vec2f addScale(final double scale,final Vec2f that)
    {
        this.x+=scale*that.x;
        this.y+=scale*that.y;
        return this;
    }

    /**
     * Multiply a given vector by a matrix and put result into current vector
     * @param mat any matrix
     * @param v any vector
     * @return current vector
     */
    /*public Vec2f setMatMultiply(final double[] mat,final Vec2f v)
    {
        this.x=mat[0]*v.x+mat[1]*v.y+mat[2]*v.z;
        this.y=mat[3]*v.x+mat[4]*v.y+mat[5]*v.z;
        this.z=mat[6]*v.x+mat[7]*v.y+mat[8]*v.z;
        return this;
    }*/

    /**
     * Multiply a given vector by the transpose of a matrix and put result into current vector
     * @param mat any matrix
     * @param v any vector
     * @return current vector
     */
   /* public Vec2f setTransposeMatMultiply(final double[] mat,final Vec2f v)
    {
        this.x=mat[0]*v.x+mat[3]*v.y+mat[6]*v.z;
        this.y=mat[1]*v.x+mat[4]*v.y+mat[7]*v.z;
        this.z=mat[2]*v.x+mat[5]*v.y+mat[8]*v.z;
        return this;
    }*/

    /**
     * Compute dot (inner) product with another vector
     * @param v vector with which dotproduct is computed
     * @return result of dot product
     */
    public double dotProduct(final Vec2f v)
    {
        return this.x*v.x+this.y*v.y;
    }


    @Override
    public String toString(){
        return "[" + x + ", " + y + "]";
    }
}
