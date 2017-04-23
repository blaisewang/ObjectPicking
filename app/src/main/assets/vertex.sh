uniform mat4 uMVPMatrix;
attribute vec3 aPosition;
varying vec3 vPosition;

void main()     
{
	gl_Position = uMVPMatrix * vec4(aPosition,1);
	vPosition = aPosition;
}
