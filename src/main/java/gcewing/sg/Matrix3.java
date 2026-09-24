package gcewing.sg;

public class Matrix3 {
   public static Matrix3 ident = new Matrix3();
   public static Matrix3[] turnRotations = new Matrix3[]{rotY((double)0.0F), rotY((double)90.0F), rotY((double)180.0F), rotY((double)270.0F)};
   public static Matrix3[] sideRotations;
   public static Matrix3[][] sideTurnRotations;
   public double[][] m = new double[][]{{(double)1.0F, (double)0.0F, (double)0.0F}, {(double)0.0F, (double)1.0F, (double)0.0F}, {(double)0.0F, (double)0.0F, (double)1.0F}};

   public static Matrix3 rotX(double deg) {
      return rot(deg, 1, 2);
   }

   public static Matrix3 rotY(double deg) {
      return rot(deg, 2, 0);
   }

   public static Matrix3 rotZ(double deg) {
      return rot(deg, 0, 1);
   }

   static Matrix3 rot(double deg, int i, int j) {
      double a = Math.toRadians(deg);
      double s = Math.sin(a);
      double c = Math.cos(a);
      Matrix3 r = new Matrix3();
      r.m[i][i] = c;
      r.m[i][j] = -s;
      r.m[j][i] = s;
      r.m[j][j] = c;
      return r;
   }

   public Matrix3 mul(Matrix3 n) {
      Matrix3 r = new Matrix3();

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 3; ++j) {
            r.m[i][j] = this.m[i][0] * n.m[0][j] + this.m[i][1] * n.m[1][j] + this.m[i][2] * n.m[2][j];
         }
      }

      return r;
   }

   public Vector3 mul(double x, double y, double z) {
      return new Vector3(x * this.m[0][0] + y * this.m[0][1] + z * this.m[0][2], x * this.m[1][0] + y * this.m[1][1] + z * this.m[1][2], x * this.m[2][0] + y * this.m[2][1] + z * this.m[2][2]);
   }

   public Vector3 imul(double x, double y, double z) {
      return new Vector3(x * this.m[0][0] + y * this.m[1][0] + z * this.m[2][0], x * this.m[0][1] + y * this.m[1][1] + z * this.m[2][1], x * this.m[0][2] + y * this.m[1][2] + z * this.m[2][2]);
   }

   public Vector3 mul(Vector3 v) {
      return this.mul(v.x, v.y, v.z);
   }

   public Vector3 imul(Vector3 v) {
      return this.imul(v.x, v.y, v.z);
   }

   static {
      sideRotations = new Matrix3[]{ident, rotX((double)180.0F), rotX((double)90.0F), rotX((double)-90.0F).mul(rotY((double)180.0F)), rotZ((double)-90.0F).mul(rotY((double)90.0F)), rotZ((double)90.0F).mul(rotY((double)-90.0F))};
      sideTurnRotations = new Matrix3[6][4];

      for(int side = 0; side < 6; ++side) {
         for(int turn = 0; turn < 4; ++turn) {
            sideTurnRotations[side][turn] = sideRotations[side].mul(turnRotations[turn]);
         }
      }

   }
}
