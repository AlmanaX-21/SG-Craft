package gcewing.sg;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class Vector3 {
   public static Vector3 zero = new Vector3((double)0.0F, (double)0.0F, (double)0.0F);
   public static Vector3 blockCenter = new Vector3((double)0.5F, (double)0.5F, (double)0.5F);
   public static Vector3 unitX = new Vector3((double)1.0F, (double)0.0F, (double)0.0F);
   public static Vector3 unitY = new Vector3((double)0.0F, (double)1.0F, (double)0.0F);
   public static Vector3 unitZ = new Vector3((double)0.0F, (double)0.0F, (double)1.0F);
   public static Vector3 unitNX = new Vector3((double)-1.0F, (double)0.0F, (double)0.0F);
   public static Vector3 unitNY = new Vector3((double)0.0F, (double)-1.0F, (double)0.0F);
   public static Vector3 unitNZ = new Vector3((double)0.0F, (double)0.0F, (double)-1.0F);
   public static Vector3 unitPYNZ = new Vector3((double)0.0F, 0.707, -0.707);
   public static Vector3 unitPXPY = new Vector3(0.707, 0.707, (double)0.0F);
   public static Vector3 unitPYPZ = new Vector3((double)0.0F, 0.707, 0.707);
   public static Vector3 unitNXPY = new Vector3(-0.707, 0.707, (double)0.0F);
   double x;
   double y;
   double z;
   public static Vector3[][] faceBases;
   public static Vec3i[] directionVec;

   public static Vector3 blockCenter(double x, double y, double z) {
      return blockCenter.add(x, y, z);
   }

   public static Vector3 blockCenter(BlockPos pos) {
      return blockCenter.add(pos);
   }

   public Vector3(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vector3(Vec3 v) {
      this(v.xCoord, v.yCoord, v.zCoord);
   }

   public Vector3(BlockPos pos) {
      this((double)pos.x, (double)pos.y, (double)pos.z);
   }

   public Vector3(Vec3i v) {
      this((double)v.getX(), (double)v.getY(), (double)v.getZ());
   }

   public Vector3(EnumFacing f) {
      this(getDirectionVec(f));
   }

   public Vec3 toVec3() {
      return Vec3.createVectorHelper(this.x, this.y, this.z);
   }

   public String toString() {
      return String.format("(%.3f,%.3f,%.3f)", this.x, this.y, this.z);
   }

   public Vector3 add(double x, double y, double z) {
      return new Vector3(this.x + x, this.y + y, this.z + z);
   }

   public Vector3 add(Vector3 v) {
      return this.add(v.x, v.y, v.z);
   }

   public Vector3 add(BlockPos pos) {
      return this.add((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
   }

   public Vector3 sub(double x, double y, double z) {
      return new Vector3(this.x - x, this.y - y, this.z - z);
   }

   public Vector3 sub(Vector3 v) {
      return this.sub(v.x, v.y, v.z);
   }

   public static Vector3 sub(double[] u, double[] v) {
      return new Vector3(u[0] - v[0], u[1] - v[1], u[2] - v[2]);
   }

   public Vector3 mul(double c) {
      return new Vector3(c * this.x, c * this.y, c * this.z);
   }

   public double dot(Vector3 v) {
      return this.dot(v.x, v.y, v.z);
   }

   public double dot(double[] v) {
      return this.dot(v[0], v[1], v[2]);
   }

   public double dot(EnumFacing f) {
      Vec3i v = getDirectionVec(f);
      return this.dot((double)v.getX(), (double)v.getY(), (double)v.getZ());
   }

   public double dot(double vx, double vy, double vz) {
      return this.x * vx + this.y * vy + this.z * vz;
   }

   public Vector3 cross(Vector3 v) {
      return new Vector3(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
   }

   public Vector3 min(Vector3 v) {
      return new Vector3(Math.min(this.x, v.x), Math.min(this.y, v.y), Math.min(this.z, v.z));
   }

   public Vector3 max(Vector3 v) {
      return new Vector3(Math.max(this.x, v.x), Math.max(this.y, v.y), Math.max(this.z, v.z));
   }

   public double length() {
      return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
   }

   public double distance(Vector3 v) {
      double dx = this.x - v.x;
      double dy = this.y - v.y;
      double dz = this.z - v.z;
      return Math.sqrt(dx * dx + dy * dy + dz * dz);
   }

   public static Vector3 unit(Vector3 v) {
      return v.mul((double)1.0F / v.length());
   }

   public static Vector3 average(Vector3... va) {
      double x = (double)0.0F;
      double y = (double)0.0F;
      double z = (double)0.0F;

      for(Vector3 v : va) {
         x += v.x;
         y += v.y;
         z += v.z;
      }

      int n = va.length;
      return new Vector3(x / (double)n, y / (double)n, z / (double)n);
   }

   public static Vector3 average(double[]... va) {
      double x = (double)0.0F;
      double y = (double)0.0F;
      double z = (double)0.0F;

      for(double[] v : va) {
         x += v[0];
         y += v[1];
         z += v[2];
      }

      int n = va.length;
      return new Vector3(x / (double)n, y / (double)n, z / (double)n);
   }

   public int floorX() {
      return (int)Math.floor(this.x);
   }

   public int floorY() {
      return (int)Math.floor(this.y);
   }

   public int floorZ() {
      return (int)Math.floor(this.z);
   }

   public int roundX() {
      return (int)Math.round(this.x);
   }

   public int roundY() {
      return (int)Math.round(this.y);
   }

   public int roundZ() {
      return (int)Math.round(this.z);
   }

   public EnumFacing facing() {
      return facing(this.x, this.y, this.z);
   }

   public BlockPos blockPos() {
      return new BlockPos(this.floorX(), this.floorY(), this.floorZ());
   }

   public static EnumFacing facing(double dx, double dy, double dz) {
      double ax = Math.abs(dx);
      double ay = Math.abs(dy);
      double az = Math.abs(dz);
      if (ay >= ax && ay >= az) {
         return dy < (double)0.0F ? EnumFacing.DOWN : EnumFacing.UP;
      } else if (ax >= az) {
         return dx > (double)0.0F ? EnumFacing.WEST : EnumFacing.EAST;
      } else {
         return dz < (double)0.0F ? EnumFacing.NORTH : EnumFacing.SOUTH;
      }
   }

   public static Vector3[] faceBasis(EnumFacing f) {
      return faceBases[f.ordinal()];
   }

   public static Vec3i getDirectionVec(EnumFacing f) {
      return directionVec[f.ordinal()];
   }

   static {
      faceBases = new Vector3[][]{{unitX, unitZ}, {unitX, unitNZ}, {unitNX, unitY}, {unitX, unitY}, {unitZ, unitY}, {unitNZ, unitY}};
      directionVec = new Vec3i[]{new Vec3i(0, -1, 0), new Vec3i(0, 1, 0), new Vec3i(0, 0, -1), new Vec3i(0, 0, 1), new Vec3i(-1, 0, 0), new Vec3i(1, 0, 0)};
   }
}
