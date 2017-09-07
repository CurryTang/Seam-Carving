import edu.princeton.cs.algs4.Picture;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.SortedSet;

/**
 * Created by czk on 17-8-21.
 */
public class SeamCarver {
    private Picture picture;
    private double[][] energyDiff;
    private int w;
    private int h;
    public SeamCarver(Picture picture) {
        this.picture = picture;
        w = width();
        h = height();
        energyDiff = new double[h][w];
        for(int i = 0;i < h;i++){
            for(int j = 0;j < w;j++){
                energyDiff[i][j] = energy(j, i);
            }
        }
    }
    public Picture picture(){
        // current picture
        return picture;
    }
    public int width(){
        // width of current picture
        return picture.width();
    }
    public int height(){
        return picture.height();

    }

    private double deltaX2(int x, int y){
        double deltaR, deltaG, deltaB;
        int left, right;
        // configure thr horizontal case
        left = x - 1;
        right = x + 1;
        if (x == 0){
            left = width() - 1;
        } else if (x == width() - 1) {
            right = 0;
        }


        deltaR = Math.abs(picture.get(left, y).getRed() - picture.get(right, y).getRed());
        deltaG = Math.abs(picture.get(left, y).getGreen() - picture.get(right, y).getGreen());
        deltaB = Math.abs(picture.get(left, y).getBlue() - picture.get(right, y).getBlue());

        return deltaR * deltaR + deltaG * deltaG + deltaB * deltaB;
    }

    private double deltaY2(int x, int y){
        double deltaR, deltaG, deltaB;
        int up, down;

        //configure the vertical case
        up = y - 1;
        down = y + 1;
        if (y == 0) {
            up = height() - 1;
        } else if (y == height() - 1){
            down = 0;
        }

        deltaR = Math.abs(picture.get(x, up).getRed() - picture.get(x, down).getRed());
        deltaG = Math.abs(picture.get(x, up).getGreen() - picture.get(x, down).getGreen());
        deltaB = Math.abs(picture.get(x, up).getBlue() - picture.get(x, down).getBlue());

        return deltaB * deltaB + deltaR * deltaR + deltaG * deltaG;


    }



    public  double energy(int x, int y){
        return deltaX2(x, y) + deltaY2(x, y);
    }

    private static double minOfThreeValue(double o1, double o2, double o3){
        double[] arr = new double[]{o1, o2, o3};
        Arrays.sort(arr);
        return arr[0];
    }

    private static int findMinIndex(double[] arr){
        int minID = 0;
        double min = arr[0];
        for(int i = 0;i < arr.length;i++){
            if(arr[i] < min){
                minID = i;
                min = arr[i];
            }
        }
        return minID;
    }

    public int[] findVerticalSeam(){
        double[][] minimumEnergy = new double[h][w];
        int[][] path = new int[h][w];
        int[] result = new int[h];
        for(int i = 0;i < w;i++){
            minimumEnergy[0][i] = energyDiff[0][i];
        }
        for(int i = 0;i < h;i++) {
            for (int j = 0; j < w; j++) {
                // Running the dynamic programming algorithm
                if (i > 0 && j > 0 && j != w - 1) {
                    minimumEnergy[i][j] = energyDiff[i][j] + minOfThreeValue(minimumEnergy[i - 1][j - 1], minimumEnergy[i - 1][j], minimumEnergy[i - 1][j + 1]);
                } else if (i != 0 && j == 0) {
                    minimumEnergy[i][j] = (minimumEnergy[i - 1][j] > minimumEnergy[i - 1][j + 1]) ? (energyDiff[i][j] + minimumEnergy[i - 1][j + 1]) :
                            (energyDiff[i][j] + minimumEnergy[i - 1][j]);
                } else if (j == w - 1 && i != 0) {
                    minimumEnergy[i][j] = (minimumEnergy[i - 1][j - 1] > minimumEnergy[i - 1][j]) ? (energyDiff[i][j] + minimumEnergy[i - 1][j]) :
                            (energyDiff[i][j] + minimumEnergy[i - 1][j - 1]);
                }
            }
        }

        int endCol = findMinIndex(minimumEnergy[h - 1]);
        result[h - 1] = endCol;
        // Construct the path
        for(int i = h - 2;i >= 0;i--){
            if (endCol == 0){
                if (minimumEnergy[i][0] + energyDiff[i + 1][endCol] == minimumEnergy[i + 1][endCol]){
                    result[i] = 0;
                    endCol = 0;
                } else {
                    result[i] = 1;
                    endCol = 1;
                }

            } else if (endCol == width() - 1){
                if (minimumEnergy[i][width() - 1] + energyDiff[i + 1][endCol] == minimumEnergy[i + 1][endCol]){
                    result[i] = width() - 1;
                    endCol = width() - 1;
                } else {
                    result[i] = width() - 2;
                    endCol = width() - 2;
                }

            } else {
                for(int col = endCol - 1; col <= endCol + 1;col++){
                    if (minimumEnergy[i][col] + energyDiff[i + 1][endCol] == minimumEnergy[i + 1][endCol]){
                        result[i] = col;
                        endCol = col;
                    }
                }
            }
        }
        return result;
    }

    private static double[][] transpose(double[][] arr) {
        double[][] newArr = new double[arr[0].length][arr.length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr[0].length; j++) {
                newArr[j][i] = arr[i][j];
            }
        }
        return newArr;
    }

    private void transpose() {
        int temp = w;
        w = h;
        h = temp;
    }

    public   int[] findHorizontalSeam() {
        transpose();
        double[][] newEnergyDiff = new double[h][w];
        double[][] oldOne = energyDiff;
        for(int i = 0;i < h;i++){
            for(int j = 0;j < w;j++){
                newEnergyDiff[i][j] = energyDiff[j][i];
            }
        }
        energyDiff = newEnergyDiff;
        int[] result = findVerticalSeam();
        energyDiff = oldOne;
        transpose();
        return result;
    }
    public    void removeHorizontalSeam(int[] seam){
        SeamRemover.removeHorizontalSeam(picture, findHorizontalSeam());
    }

    public    void removeVerticalSeam(int[] seam){
        SeamRemover.removeVerticalSeam(picture, findVerticalSeam());
    }

    public static void main(String[] args){
        int[][] test = new int[3][4];
        for(int i = 0;i < 3;i++){
            for(int j = 0;j < 4;j++){
                System.out.println(test[i][j]);
            }
        }
    }
}
