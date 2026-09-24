package gcewing.sg;

public class Utils {
   public static double normaliseAngle(double a) {
      a %= (double)360.0F;
      if (a < (double)0.0F) {
         a += (double)360.0F;
      }

      return a;
   }

   public static double addAngle(double a, double b) {
      return normaliseAngle(a + b);
   }

   public static double diffAngle(double a, double b) {
      double d = a > b ? a - b : b - a;
      if (d > (double)180.0F) {
         d -= (double)360.0F;
      }

      if (a > b) {
         d = -d;
      }

      return d;
   }

   public static double relaxAngle(double a, double target, double rate) {
      return addAngle(a, rate * diffAngle(a, target));
   }

   public static double interpolateAngle(double a, double b, double t) {
      return addAngle(a, t * diffAngle(a, b));
   }
}
