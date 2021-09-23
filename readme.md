
# Simple Ray Tracing 
![Preview](preview.jpg?raw=true "Preview")
## Context 
This project was an university assignment during the first year of my master degree in computer science (2021). 
We had to do a simple ray tracer in Java. The ray tracer hat to include reflection and refraction of at least order 2. The program had to output a picture (the result of the ray tracing). 
Because a one shot picture was not practical I decided to perform the rendering in real time, so I also added multi threading support. At last I added texturing, which was not required in the vanilla project. GPU processing was not allowed so every calculation is processed by the CPU.

## Build from sources
This project was developed under the Eclipse IDE so an eclipse project file is available. You can also build it with [build.bat](./build.bat), or adapt the script for other platforms. You may need specific platform libraries for OpenGL to work. Windows library are included.  