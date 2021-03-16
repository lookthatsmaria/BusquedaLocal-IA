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
        int X1, Y1, X2,  Y2;
        for(int i = 0; i < m; ++i){
            if (i < n) {X1 = datacenters.get(i).getCoordX(); Y1 = datacenters.get(i).getCoordY(); }
            else { X1 = sensors.get(i-n).getCoordX(); Y1 = sensors.get(i-n).getCoordY(); }
            for(int j = 0; j < m; ++j){
                dist[i][j] = 0; map[i][j]=0;
                if (i != j && (i >= n || j >= n)){
                    if (i >= n){
                        if (j < n) {
                            X2 = datacenters.get(j).getCoordX();
                            Y2 = datacenters.get(j).getCoordY();
                            if ((i < (25+n)+j*25) && (i >= (25+n)+(j-1)*25)) {
                                double data = sensors.get(i-n).getCapacidad();
                                map[i][j] = data;
                                map[j][j] += data;
                                map[i][i] =  j;
                            }
                        }
                        else {
                            X2 = sensors.get(j - n).getCoordX();
                            Y2 = sensors.get(j - n).getCoordY();
                            if (i >= ((25*n)+n) && ((i-100) < (3+n)+(j-n)*3) && ((i-100) >= (3+n)+(j-n-1)*3)) {
                                double data = sensors.get(i-n).getCapacidad();
                                double c = map[j][j];
                                int cd = (int) c;
                                map[i][j] = data;
                                map[j][cd] += data;
                                map[cd][cd] += data;
                                map[i][i] = j;
                            }
                        }
                        dist[i][j] = distance(X1, Y1, X2, Y2);
                    }
                }
            }
        }
       print_map();

    }

    public void initialSolution2(){ // falta mirar el caso en el que no se pueda evitar perdidas de datos
        int X1, Y1, X2,  Y2;
        boolean success;
        ArrayList<Integer> sensor_not_connected = new ArrayList<>();
        for(int i = 0; i < m; ++i) {
            success = false;
            if (i < n) {
                X1 = datacenters.get(i).getCoordX();
                Y1 = datacenters.get(i).getCoordY();
            } else {
                X1 = sensors.get(i - n).getCoordX();
                Y1 = sensors.get(i - n).getCoordY();
            }
            for (int j = 0; j < m; ++j) {
                dist[i][j] = 0;
                map[i][j] = 0;
                if (i != j && (i >= n || j >= n)) {
                    if (i >= n) {
                        if (j < n) {
                            X2 = datacenters.get(j).getCoordX();
                            Y2 = datacenters.get(j).getCoordY();
                            double capture = sensors.get(i - n).getCapacidad();
                            if (canConnect(j, 25) && ((map[j][j] + capture) <= (150))) {
                                map[i][j] = capture;
                                map[j][j] += capture;
                                map[i][i] =  j;
                                success = true;
                                break;
                            }
                        } else {
                            X2 = sensors.get(j - n).getCoordX();
                            Y2 = sensors.get(j - n).getCoordY();
                            double capture = sensors.get(i - n).getCapacidad();
                            double c = map[j][j];
                            int cd = (int) c;
                            if (canConnect(j, 3) && ((map[j][cd] + capture) <= (3 * sensors.get(j - n).getCapacidad())) && ((map[cd][cd] + capture) <= (150)) ) {
                                map[i][j] = capture;
                                map[j][cd] += capture;
                                map[cd][cd] += capture;
                                map[i][i] = j;
                                success = true;
                                break;
                            }
                        }
                        dist[i][j] = distance(X1, Y1, X2, Y2);
                    }
                }
            }
            if (i >= n && !success) {
                sensor_not_connected.add(i);
            }
        }
        int tam = sensor_not_connected.size();
        int i;
        if(tam > 0){
            System.out.println("sensors not connected");
            for(int x = 0; x <tam;++x){
                for(int j = n; j < m; ++j){
                    if (canConnect(j,3)){
                        i = sensor_not_connected.get(x);
                        double capture = sensors.get(i - n).getCapacidad();
                        double c = map[j][j];
                        int cd = (int) c;
                        map[i][j] = capture;
                        map[j][cd] += capture;
                        map[cd][cd] += capture;
                        map[i][i] = j;
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
        double c = map[i][i];
        int cd = (int) c;
        return dist[i][j] * map[i][cd];
    }

    public void print_map(){
        for (int i = 0; i < m; ++i) {
            System.out.print("fila: " + i + " ");
            for (int j = 0; j < m; ++j)
                System.out.print(map[i][j] + " ");
            System.out.println();
        }
    }



}