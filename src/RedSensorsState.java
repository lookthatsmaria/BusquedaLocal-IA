import java.util.*;
import IA.Red.*;

public class RedSensorsState {
    private final ArrayList<IA.Red.Centro> datacenters;
    private final ArrayList<IA.Red.Sensor> sensors;
    private double [][] map;
    private final double [][] dist;
    private final int n, m;

    public RedSensorsState(int ncent, int nsens, int seedc, int seeds, boolean option){
        datacenters = new CentrosDatos(ncent, seedc);
        sensors = new Sensores(nsens, seeds);
        n = ncent;
        m = ncent+nsens;
        map = new double[m][m];
        dist = new double[m][m];
        if (option) initialSolution1();
        else initialSolution2();
    }

    public void initialSolution1(){
        int src_X, src_Y, dst_X,  dst_Y;
        for(int i = 0; i < m; ++i){
            if (i < n) {
							src_X = datacenters.get(i).getCoordX();
							src_Y = datacenters.get(i).getCoordY();
						}
            else { 
							src_X = sensors.get(i-n).getCoordX();
							src_Y = sensors.get(i-n).getCoordY();
						}
            for(int j = 0; j < m; ++j){
                dist[i][j] = 0; map[i][j]=0;
                if (i != j && (i >= n || j >= n)){
                    if (i >= n){
                        if (j < n) {
                            dst_X = datacenters.get(j).getCoordX();
                            dst_Y = datacenters.get(j).getCoordY();
                            if ((i < (25+n)+j*25) && (i >= (25+n)+(j-1)*25)) {
                                double capture = sensors.get(i-n).getCapacidad();
                                map[i][j] = capture;
                                map[j][j] += capture;
                                map[i][i] =  j;
                                break;
                            }
                        }
                        else {
                            dst_X = sensors.get(j - n).getCoordX();
                            dst_Y = sensors.get(j - n).getCoordY();
                            if (i >= ((25*n)+n) 
																&& ((i-(25*n)) < (3+n)+(j-n)*3) 
																&& ((i-(25*n)) >= (3+n)+(j-n-1)*3)) 
														{
                                double capture = sensors.get(i-n).getCapacidad();
                                double c = map[j][j];
                                int cd = (int) c;
                                map[i][j] = capture;
                                map[j][cd] += capture;
                                if (cd < n) map[cd][cd] += capture;
                                map[i][i] = j;
                                break;
                            }
                        }
                        dist[i][j] = distance(src_X, src_Y, dst_X, dst_Y);
                    }
                }
            }
        }
       print_map();

    }

    public void initialSolution2(){
        int src_X, src_Y, dst_X,  dst_Y;
        for(int i = 0; i < m; ++i) {
            if (i < n) {
                src_X = datacenters.get(i).getCoordX();
                src_Y = datacenters.get(i).getCoordY();
            } else {
                src_X = sensors.get(i - n).getCoordX();
                src_Y = sensors.get(i - n).getCoordY();
            }
            for (int j = 0; j < m; ++j) {
                dist[i][j] = 0;
                map[i][j] = 0;
                if (i != j && (i >= n || j >= n)) {
                    if (i >= n) {
                        if (j < n) {
                            dst_X = datacenters.get(j).getCoordX();
                            dst_Y = datacenters.get(j).getCoordY();
                            double capture = sensors.get(i - n).getCapacidad();
                            if (canConnect(j, 25) && ((map[j][j] + capture) <= (150))) {
                                map[i][j] = capture;
                                map[j][j] += capture;
                                map[i][i] =  j;
                                break;
                            }
                        } else {
                            dst_X = sensors.get(j - n).getCoordX();
                            dst_Y = sensors.get(j - n).getCoordY();
                            double capture = sensors.get(i - n).getCapacidad();
                            double c = map[j][j];
                            int cd = (int) c;
                            if (canConnect(j, 3) 
																&& ((map[j][cd] + capture) <= (3 * sensors.get(j - n).getCapacidad()))
																&& ((map[cd][cd] + capture) <= (150)) ) 
														{
                                map[i][j] = capture;
                                map[j][cd] += capture;
                                if (cd < n) map[cd][cd] += capture;
                                map[i][i] = j;
                                break;
                            }
                        }
                        dist[i][j] = distance(src_X, src_Y, dst_X, dst_Y);
                    }
                }
            }
        }
         print_map();

    }

    public boolean canConnect(int j, int limit){
        int numConnexions = 0;
        for(int i = n; i < m; ++i){
            if (map[i][j] > 0) ++numConnexions;
            if (numConnexions >= limit) return false;
        }
        return true;
    }

    public double distance(int X1, int Y1, int X2, int Y2){
        return ((double) X1 - (double) X2)*((double) X1 - (double) X2)+((double) Y1 - (double) Y2)*((double) Y1 - (double) Y2);
    }

    public double getDistance(int i, int j){
        if (i < 0 || i >= m || j < 0 || j >= m) return -1;
        return dist[i][j];
    }

    public double getCost(int i, int j){
        if (i < 0 || i >= m || j < 0 || j >= m) return -1;
        return dist[i][j] * map[i][j];
    }

    public void print_map(){
        for (int i = 0; i < m; ++i) {
            System.out.print("fila: " + i + " ");
            for (int j = 0; j < m; ++j)
                System.out.print(map[i][j] + " ");
            System.out.println();
        }
    }

    public void swapConnections(int i, int j, int newc){
        double capture = map[i][j];
        double c;
        if(j < n) map[j][j] -= capture;
        else{
            c = map[j][j];
            int cd = (int) c;
            map[j][cd] -= capture;
            if (cd < n) map[cd][cd] -= capture;
        }
        if(newc < n){
            map[i][newc] = capture;
            map[newc][newc] += capture;
        }
        else{
            c = map[newc][newc];
            int cd = (int) c;
            map[i][newc] = capture;
            map[newc][cd] += capture;
            if (cd < n) map[cd][cd] += capture;
        }
        map[i][j] = 0;
        map[i][i] =  newc;
    }


}
