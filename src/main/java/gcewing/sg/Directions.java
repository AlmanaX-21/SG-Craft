package gcewing.sg;

public class Directions {
   static int[][] globalToLocalSideTable = new int[][]{{0, 0, 0, 0}, {1, 1, 1, 1}, {2, 5, 3, 4}, {3, 4, 2, 5}, {4, 2, 5, 3}, {5, 3, 4, 2}};

   static int globalToLocalSide(int side, int rotation) {
      return globalToLocalSideTable[side][rotation];
   }
}
