#version 330 core

in vec2 v_tCoords;
out vec4 out_Color;

uniform sampler2D uTexture;

void main(){
	out_Color = texture2D(uTexture, v_tCoords);
}
