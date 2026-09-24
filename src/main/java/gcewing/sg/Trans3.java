package gcewing.sg;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class Trans3 {
   public static Trans3 ident;
   public static Trans3 blockCenter;
   public static Trans3[][] sideTurnRotations;
   public Vector3 offset;
   public Matrix3 rotation;
   public double scaling;

   public static Trans3 blockCenter(BlockPos pos) {
      return new Trans3(Vector3.blockCenter(pos));
   }

   public static Trans3 sideTurn(int side, int turn) {
      return sideTurnRotations[side][turn];
   }

   public static Trans3 sideTurn(double x, double y, double z, int side, int turn) {
      return sideTurn(new Vector3(x, y, z), side, turn);
   }

   public static Trans3 blockCenterSideTurn(int side, int turn) {
      return sideTurn(Vector3.blockCenter, side, turn);
   }

   public static Trans3 sideTurn(Vector3 v, int side, int turn) {
      return new Trans3(v, Matrix3.sideTurnRotations[side][turn]);
   }

   public Trans3(Vector3 v) {
      this(v, Matrix3.ident);
   }

   public Trans3(Vector3 v, Matrix3 m) {
      this(v, m, (double)1.0F);
   }

   public Trans3(Vector3 v, Matrix3 m, double s) {
      this.offset = v;
      this.rotation = m;
      this.scaling = s;
   }

   public Trans3(double dx, double dy, double dz) {
      this(new Vector3(dx, dy, dz), Matrix3.ident, (double)1.0F);
   }

   public Trans3(BlockPos pos) {
      this((double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)0.5F, (double)pos.getZ() + (double)0.5F);
   }

   public Trans3 translate(Vector3 v) {
      return v == Vector3.zero ? this : this.translate(v.x, v.y, v.z);
   }

   public Trans3 translate(double dx, double dy, double dz) {
      return new Trans3(this.offset.add(this.rotation.mul(dx * this.scaling, dy * this.scaling, dz * this.scaling)), this.rotation, this.scaling);
   }

   public Trans3 rotate(Matrix3 m) {
      return new Trans3(this.offset, this.rotation.mul(m), this.scaling);
   }

   public Trans3 rotX(double deg) {
      return this.rotate(Matrix3.rotX(deg));
   }

   public Trans3 rotY(double deg) {
      return this.rotate(Matrix3.rotY(deg));
   }

   public Trans3 rotZ(double deg) {
      return this.rotate(Matrix3.rotZ(deg));
   }

   public Trans3 scale(double s) {
      return new Trans3(this.offset, this.rotation, this.scaling * s);
   }

   public Trans3 side(EnumFacing dir) {
      return this.side(dir.ordinal());
   }

   public Trans3 side(int i) {
      return this.rotate(Matrix3.sideRotations[i]);
   }

   public Trans3 turn(int i) {
      return this.rotate(Matrix3.turnRotations[i]);
   }

   public Trans3 t(Trans3 t) {
      return new Trans3(this.offset.add(this.rotation.mul(t.offset).mul(this.scaling)), this.rotation.mul(t.rotation), this.scaling * t.scaling);
   }

   public Vector3 p(double x, double y, double z) {
      return this.p(new Vector3(x, y, z));
   }

   public Vector3 p(Vector3 u) {
      return this.offset.add(this.rotation.mul(u.mul(this.scaling)));
   }

   public Vector3 ip(double x, double y, double z) {
      return this.ip(new Vector3(x, y, z));
   }

   public Vector3 ip(Vector3 u) {
      return this.rotation.imul(u.sub(this.offset)).mul((double)1.0F / this.scaling);
   }

   public Vector3 v(double x, double y, double z) {
      return this.v(new Vector3(x, y, z));
   }

   public Vector3 iv(double x, double y, double z) {
      return this.iv(new Vector3(x, y, z));
   }

   public Vector3 v(Vec3i u) {
      return this.v((double)u.getX(), (double)u.getY(), (double)u.getZ());
   }

   public Vector3 iv(Vec3i u) {
      return this.iv((double)u.getX(), (double)u.getY(), (double)u.getZ());
   }

   public Vector3 v(Vector3 u) {
      return this.rotation.mul(u.mul(this.scaling));
   }

   public Vector3 v(EnumFacing f) {
      return this.v(Vector3.getDirectionVec(f));
   }

   public Vector3 iv(EnumFacing f) {
      return this.iv(Vector3.getDirectionVec(f));
   }

   public Vector3 iv(Vector3 u) {
      return this.rotation.imul(u).mul((double)1.0F / this.scaling);
   }

   public Vector3 iv(Vec3 u) {
      return this.iv(u.xCoord, u.yCoord, u.zCoord);
   }

   public AxisAlignedBB t(AxisAlignedBB box) {
      return boxEnclosing(this.p(box.minX, box.minY, box.minZ), this.p(box.maxX, box.maxY, box.maxZ));
   }

   public AxisAlignedBB box(Vector3 p0, Vector3 p1) {
      return boxEnclosing(this.p(p0), this.p(p1));
   }

   public static int turnFor(Entity e, int side) {
      if (side > 1) {
         return 0;
      } else {
         int rot = Math.round(e.rotationYaw / 90.0F);
         if (side == 0) {
            rot = 2 - rot;
         } else {
            rot = 2 + rot;
         }

         return rot & 3;
      }
   }

   public static AxisAlignedBB boxEnclosing(Vector3 p, Vector3 q) {
      return AxisAlignedBB.getBoundingBox(Math.min(p.x, q.x), Math.min(p.y, q.y), Math.min(p.z, q.z), Math.max(p.x, q.x), Math.max(p.y, q.y), Math.max(p.z, q.z));
   }

   public EnumFacing t(EnumFacing f) {
      return this.v(f).facing();
   }

   public EnumFacing it(EnumFacing f) {
      return this.iv(f).facing();
   }

   public void addBox(Vector3 p0, Vector3 p1, List list) {
      this.addBox(p0.x, p0.y, p0.z, p1.x, p1.y, p1.z, list);
   }

   public void addBox(double x0, double y0, double z0, double x1, double y1, double z1, List list) {
      AxisAlignedBB box = boxEnclosing(this.p(x0, y0, z0), this.p(x1, y1, z1));
      list.add(box);
   }

   static {
      ident = new Trans3(Vector3.zero);
      blockCenter = new Trans3(Vector3.blockCenter);
      sideTurnRotations = new Trans3[6][4];

      for(int side = 0; side < 6; ++side) {
         for(int turn = 0; turn < 4; ++turn) {
            sideTurnRotations[side][turn] = new Trans3(Vector3.zero, Matrix3.sideTurnRotations[side][turn]);
         }
      }

   }
}
