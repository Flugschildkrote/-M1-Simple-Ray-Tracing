#version 330 core

layout (location=0) in vec2 in_pos;
layout (location=1) in vec2 in_tCoords;

out vec2 v_tCoords;


void main(){

	v_tCoords = (in_tCoords+vec2(1.0, 1.0))/2.0;
	gl_Position = vec4(in_pos, 1.0, 1.0);
}


