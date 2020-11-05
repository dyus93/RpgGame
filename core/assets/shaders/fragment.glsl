#version 120

varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float time;
uniform float px;
uniform float py;

void main() {
  gl_FragColor = texture2D(u_texture, v_texCoords);
  float xx = px + v_texCoords.x;
  float yy = py + v_texCoords.y;
  gl_FragColor.rgb += 0.12 * sin(xx * 3 + time / 2 + yy * 3) + 0.09 * sin(-xx * 5 - time / 3 + yy * 5)  + 0.11 * sin(-xx * 5 - time / 3 + yy * 5) * cos(time + xx);
}
