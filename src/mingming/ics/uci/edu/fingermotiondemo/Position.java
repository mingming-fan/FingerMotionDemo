package mingming.ics.uci.edu.fingermotiondemo;

public class Position {
 public float x;
 public float y;
 Position()
 {
	 x = 0.0f;
	 y = 0.0f;
 }
 
 Position(float _x, float _y)
 {
	 x = _x;
	 y = _y;
 }
 
 public void updatePos(float _x, float _y)
 {
	 x = _x;
	 y = _y;
 }
}
