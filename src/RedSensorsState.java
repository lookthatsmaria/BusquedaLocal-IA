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

    public void initialSolution1(){ //Falta contabilizar todos los costes !! para ver si hay pérdidas o no.
        int X1, Y1, X2,  Y2;
        for(int i = 0; i < m; ++i){
            System.out.print("fila: "+i+"  ");
            if (i < n) {X1 = datacenters.get(i).getCoordX(); Y1 = datacenters.get(i).getCoordY(); }
            else { X1 = sensors.get(i-n).getCoordX(); Y1 = sensors.get(i-n).getCoordY(); }
            for(int j = 0; j < m; ++j){
                dist[i][j] = 0; map[i][j]=0;
                if (i != j && (i >= n || j >= n)){
                    if (i >= n){
                        if (j < n) {
                            X2 = datacenters.get(j).getCoordX();
                            Y2 = datacenters.get(j).getCoordY();
                            if ((i < (25+n)+j*25) && (i >= (25+n)+(j-1)*25)) map[i][j] = sensors.get(i-n).getCapacidad();
                        }
                        else {
                            X2 = sensors.get(j - n).getCoordX();
                            Y2 = sensors.get(j - n).getCoordY();
                            if (i >= ((25*n)+n) && ((i-100) < (3+n)+(j-n)*3) && ((i-100) >= (3+n)+(j-n-1)*3)) map[i][j] = sensors.get(i-n).getCapacidad();
                        }
                        dist[i][j] = distance(X1, Y1, X2, Y2);
                    }
                }

                System.out.print(map[i][j]+" ");
            }
            System.out.println("");
        }

    }

    public void initialSolution2() { //Falta contabilizar todos los costes !! para ver si hay pérdidas o no.
        int X1, Y1, X2, Y2;
        for (int i = 0; i < m; ++i) {
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
                            if ((i < (25 + n) + j * 25) && (i >= (25 + n) + (j - 1) * 25))
                                map[i][j] = sensors.get(i - n).getCapacidad();
                            dist[i][j] = distance(X1, Y1, X2, Y2);
                        }
                    }
                }

            }
        }
        int i = n * 25 + n;
        boolean success;
        boolean perdida = false;
        int sensor_not_connected = -1;
        while (i < m) {
            success = false;
            for (int j = n; j < m; ++j) {
                double capture = sensors.get(i - n).getCapacidad();
                if (canConnect(j) && ((dataVolume(j) + capture) <= (3 * sensors.get(j - n).getCapacidad()))) {
                    System.out.println("i: " + i + " j: " + j + "    capture: " + (dataVolume(j) + capture) + "   capacity: " + (3 * sensors.get(j - n).getCapacidad())+" connection succesfull");
                    map[i][j] = capture;
                    success = true;
                    break;
                }
            }
            if (!perdida && !success) {
                perdida = !success;
                sensor_not_connected = i;
            }
            ++i;
        }
        System.out.println("perdida: "+perdida+" sensor: "+sensor_not_connected);
        for (i = 0; i < m; ++i) {
            System.out.print("fila: " + i + " ");
            for (int j = n; j < m; ++j)
                System.out.print(map[i][j] + " ");
            System.out.println("");
        }
      }

    public boolean canConnect(int j){
        int numConnexions = 0;
        for(int i = n; i < m; ++i){
            if (map[i][j] > 0) ++numConnexions;
            if (numConnexions >= 3) {
                System.out.println("no se puede conectar");
                return false;
            }
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
        return dist[i][j] * dataVolume(i);
    }

    public double dataVolume(int j){
        int connexions = 0;
        double valor = 0;
        double sum_cost = 0;
        for(int i = n; i < m; ++i){
            if (connexions >= 3) break;
            valor = map[i][j];
            if (valor > 0) {
                sum_cost += valor;
                ++connexions;
            }
        }

        return sum_cost;
    }


}