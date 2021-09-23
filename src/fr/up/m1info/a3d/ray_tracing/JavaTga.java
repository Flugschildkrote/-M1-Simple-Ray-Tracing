package fr.up.m1info.a3d.ray_tracing;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author P. Meseure based on a Java Adaptation of a C code by B. Debouchages (M1, 2018-2019)
 */
public class JavaTga
{
    /**
     * 
     * @param fout : output file stream
     * @param s : short to write to disc in little endian
     */
    private static void writeShort(FileOutputStream fout,int n) throws IOException
    {
        fout.write(n&255);
        fout.write((n>>8)&255);
    }

    /**
     * 
     * @param filename name of final TGA file
     * @param buffer buffer that contains the image. 3 bytes per pixel ordered this way : Blue, Green, Red
     * @param width Width of the image
     * @param height Height of the image
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws IOException 
     */
    private static void saveTGA(String filename, byte buffer[], int width, int height) throws IOException, UnsupportedEncodingException {

        FileOutputStream fout = new FileOutputStream(new File(filename));

        fout.write(0); // Comment size, no comment
        fout.write(0); // Colormap type: No colormap
        fout.write(2); // Image type
        writeShort(fout,0); // Origin
        writeShort(fout,0); // Length
        fout.write(0); // Depth
        writeShort(fout,0); // X origin
        writeShort(fout,0); // Y origin
        writeShort(fout,width); // Width of the image
        writeShort(fout,height); // Height of the image
        fout.write(24); // Pixel size in bits (24bpp)
        fout.write(0); // Descriptor

        /* Write the buffer */
        fout.write(buffer);

        fout.close();
    }

    
    
    
    /**
     * @param args no command line arguments
     */
    public static void main(String[] args) {
        int w=1024;
        int h=768;
        byte buffer[]=new byte[3*w*h];
        
        //Scene s = new Scene();
        
        for(int row = 0; row < h; row++){ // for each row of the image
            for(int col = 0; col < w; col++){ // for each column of the image
                
                int index = 3*((row*w)+col); // compute index of color for pixel (x,y) in the buffer
                
                // Ensure that the pixel is black
                buffer[index]=0; // blue : take care, blue is the first component !!!
                buffer[index+1]=0; // green
                buffer[index+2]=0; // red (red is the last component !!!)
                
                // Depending on the x position, select a color... 
                float D = 10.0f;
                float x = (float)(col-w/2)/(float)h;
                float y = (float)(row-h/2)/(float)h;
                float z = -D;
                                
                Ray ray = new Ray(new Vec3f(x, y, z), new Vec3f(0.0f, 0.0f, 0.0f));
                
               // Vec3f color = s.findColor(ray);
              /*  byte r = (byte) Math.round(color.x * 255.0f);
                byte g = (byte) Math.round(color.y * 255.0f);
                byte b = (byte) Math.round(color.z * 255.0f);

                buffer[index] = r;
                buffer[index+1] = g;
                buffer[index+2] = b;*/

                //if (col<w/3) buffer[index]=(byte)255; // Blue in the left part of the image
                //else if (col<2*w/3) buffer[index+1]=(byte)255; // Green in the middle
                //else buffer[index+2]=(byte)255; // Red in the right part
            }
        }
        try {
            saveTGA("imagetest.tga",buffer,w,h);
        }
        catch(Exception e)
        {
            System.err.println("TGA file not created :"+e);
        }
    }  
}

