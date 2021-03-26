import java.util.*;
import IA.Red.*;

public class RedSensorsState {
    private final ArrayList<IA.Red.Centro> datacenters;
    private final ArrayList<IA.Red.Sensor> sensors;
    private double [][] adjacencyMatrix;
    private int [] connexions;
    private double [] thropt;
    private double [] dataDC;
    private final double [][] dist;
    private final int ncent, nElements, nsens;
    private double maxData;

    public RedSensorsState(int ncent, int nsens, int seedc, int seeds, int option){
        datacenters = new CentrosDatos(ncent, seedc);
        sensors = new Sensores(nsens, seeds);
        this.ncent = ncent;
        nElements = ncent+nsens;
        this.nsens = nsens;
        adjacencyMatrix = new double[nElements][nElements];
        connexions = new int[nsens];
        for(int i = 0; i < nsens;++i) connexions[i] = -1;
        thropt = new double[nsens];
        dist = new double[nElements][nElements];
        dataDC = new double[ncent];
        calculate_maxData();
        switch (option) {
            case 2 -> initialSolution2_v2();
            case 3 -> initialSolutionTestLoop();
            default -> initialSolution1_v2();
        }
    }

    public RedSensorsState(int ncent, int nsens, double [][] dist, double [][] adjacencyMatrix, ArrayList<IA.Red.Centro> datacenters, ArrayList<IA.Red.Sensor> sensors){
        this.ncent = ncent;
        nElements = ncent+nsens;
        this.nsens = nsens;
        this.dist = dist;
        this.adjacencyMatrix = adjacencyMatrix;
        this.datacenters = datacenters;
        this.sensors = sensors;
    }

    public RedSensorsState(int ncent, int nsens, double [][] dist, int []connexions, double[] thropt,double[] dataDC, ArrayList<IA.Red.Centro> datacenters, ArrayList<IA.Red.Sensor> sensors, double maxData){
        this.ncent = ncent;
        nElements = ncent+nsens;
        this.nsens = nsens;
        this.dist = dist;
        this.connexions = connexions;
        this.datacenters = datacenters;
        this.sensors = sensors;
        this.thropt = thropt;
        this.dataDC = dataDC;
        this.maxData = maxData;
    }

    public void initialSolution1_v2(){
        int j = 0;
        double capture;
        int count = 1;
        for(int i = 0; i < nsens; ++i){
            capture = sensors.get(i).getCapacidad();
            thropt[i] = capture;
            if (j < ncent){
                dataDC[j] += capture;
                connexions[i] =  j;
                if ((i+1)%25==0) ++j;
            }
            else{
                connexions[i] = j;
                propagateThroughput_v2(thropt[j-ncent], sensors.get(j-ncent).getCapacidad()*3, sensors.get(i).getCapacidad(), connexions[j-ncent], j);
                if (count%3==0) ++j;
                ++count;
            }

        }
        print_map_v2();
        initializeDist();

    }

    public void initialSolution2_v2(){
        int j;
        double capture;
        for(int i = 0; i < nsens; ++i) {
            j = 0;
            capture = sensors.get(i).getCapacidad();
            boolean connected = false;
            while (j < nElements && !connected) {
                if (j < ncent) {
                    if (canConnect_v2(j, 25) && ((dataDC[j] + capture) <= (150))){
                        connexions[i] = j;
                        thropt[i] = capture;
                        dataDC[j] += capture;
                        connected = true;
                    }
                }
                else {
                    if (canConnect_v2(j, 3)
                            && ((thropt[j-ncent] + capture) <= (3 * sensors.get(j-ncent).getCapacidad()))){
                        connexions[i] = j;
                        propagateThroughput_v2(thropt[j-ncent], sensors.get(j-ncent).getCapacidad()*3, sensors.get(i).getCapacidad(), connexions[j-ncent], j);
                        thropt[i] = capture;
                        connected = true;
                        j=ncent;
                    }
                }
                ++j;
            }
        }

        print_map_v2();
        initializeDist();

    }

    public void calculate_maxData(){
        double sum = 0;
        for(int sens = 0; sens < nsens; ++sens) sum += sensors.get(sens).getCapacidad();
        maxData = sum;
    }

    public void initializeDist(){
        int src_X, src_Y, dst_X,  dst_Y;
        for(int i = 0; i < nElements; ++i) {
            if (i < ncent) {
                src_X = datacenters.get(i).getCoordX();
                src_Y = datacenters.get(i).getCoordY();
            } else {
                src_X = sensors.get(i - ncent).getCoordX();
                src_Y = sensors.get(i - ncent).getCoordY();
            }
            for (int j = 0; j < nElements; ++j) {
                if (j < ncent) {
                    dst_X = datacenters.get(j).getCoordX();
                    dst_Y = datacenters.get(j).getCoordY();
                } else {
                    dst_X = sensors.get(j - ncent).getCoordX();
                    dst_Y = sensors.get(j - ncent).getCoordY();
                }
                dist[i][j] = distance(src_X, src_Y, dst_X, dst_Y);
            }
        }
    }

    public void propagateThroughput(double throughput, double capacity,double capture, int cd, int j){
        if (capture + throughput <= capacity) adjacencyMatrix[j][cd] += capture;
        else{
            capture = capacity-adjacencyMatrix[j][cd];
            adjacencyMatrix[j][cd] = capacity;
        }
        if(capture != 0 && cd >= ncent){
            double c = adjacencyMatrix[cd][cd];
            int next_node = (int) c;
            propagateThroughput(adjacencyMatrix[cd][next_node],
                    sensors.get(cd- ncent).getCapacidad()*3,
                    capture,
                    next_node,
                    cd );
        }
    }

    public void propagateThroughput_v2(double throughput, double capacity,double capture, int cd, int j){
        if (capture + throughput <= capacity) thropt[j-ncent] += capture;
        else {
            capture = capacity - thropt[j - ncent];
            thropt[j - ncent] = capacity;
        }
        if(capture >= 0) {
            if (cd >= ncent) {
                int next_node = connexions[cd - ncent];
                propagateThroughput_v2(thropt[cd - ncent],
                        sensors.get(cd-ncent).getCapacidad() * 3,
                        capture,
                        next_node,
                        cd);
            }
            else if (dataDC[cd] + capture <= 150)dataDC[cd] += capture;
        }
    }

    public void initialSolution1(){
        int src_X, src_Y, dst_X,  dst_Y;
        for(int i = 0; i < nElements; ++i){
            if (i < ncent) {
							src_X = datacenters.get(i).getCoordX();
							src_Y = datacenters.get(i).getCoordY();
						}
            else { 
							src_X = sensors.get(i- ncent).getCoordX();
							src_Y = sensors.get(i- ncent).getCoordY();
						}
            for(int j = 0; j < nElements; ++j){
                dist[i][j] = 0; adjacencyMatrix[i][j]=0;
                if (i != j && (i >= ncent || j >= ncent)){
                    if (i >= ncent){
                        if (j < ncent) {
                            dst_X = datacenters.get(j).getCoordX();
                            dst_Y = datacenters.get(j).getCoordY();
                            if ((i < (25+ ncent)+j*25) && (i >= (25+ ncent)+(j-1)*25)) {
                                double capture = sensors.get(i- ncent).getCapacidad();
                                adjacencyMatrix[i][j] = capture;
                                adjacencyMatrix[i][i] =  j;
                                break;
                            }
                        }
                        else {
                            dst_X = sensors.get(j - ncent).getCoordX();
                            dst_Y = sensors.get(j - ncent).getCoordY();
                            if (i >= ((25* ncent)+ ncent)
																&& ((i-(25* ncent)) < (3+ ncent)+(j- ncent)*3)
																&& ((i-(25* ncent)) >= (3+ ncent)+(j- ncent -1)*3))
														{
                                double capture = sensors.get(i- ncent).getCapacidad();
                                double c = adjacencyMatrix[j][j];
                                int cd = (int) c;
                                adjacencyMatrix[i][j] = capture;
                                propagateThroughput(adjacencyMatrix[j][cd], sensors.get(j- ncent).getCapacidad()*3, capture, cd, j );
                                adjacencyMatrix[i][i] = j;
                                break;
                            }
                        }
                        dist[i][j] = distance(src_X, src_Y, dst_X, dst_Y);
                    }
                }
            }
        }
       print_map();
        initialSolution1_v2();
    }

    public void initialSolution2(){
        int src_X, src_Y, dst_X,  dst_Y;
        for(int i = 0; i < nElements; ++i) {
            if (i < ncent) {
                src_X = datacenters.get(i).getCoordX();
                src_Y = datacenters.get(i).getCoordY();
            } else {
                src_X = sensors.get(i - ncent).getCoordX();
                src_Y = sensors.get(i - ncent).getCoordY();
            }
            for (int j = 0; j < nElements; ++j) {
                dist[i][j] = 0;
                adjacencyMatrix[i][j] = 0;
                if (i != j && (i >= ncent || j >= ncent)) {
                    if (i >= ncent) {
                        if (j < ncent) {
                            dst_X = datacenters.get(j).getCoordX();
                            dst_Y = datacenters.get(j).getCoordY();
                            double capture = sensors.get(i - ncent).getCapacidad();
                            if (canConnect(j, 25) && ((adjacencyMatrix[j][j] + capture) <= (150))) {
                                adjacencyMatrix[i][j] = capture;
                                adjacencyMatrix[i][i] =  j;
                                break;
                            }
                        } else {
                            dst_X = sensors.get(j - ncent).getCoordX();
                            dst_Y = sensors.get(j - ncent).getCoordY();
                            double capture = sensors.get(i - ncent).getCapacidad();
                            double c = adjacencyMatrix[j][j];
                            int cd = (int) c;
                            if (canConnect(j, 3) 
																&& ((adjacencyMatrix[j][cd] + capture) <= (3 * sensors.get(j - ncent).getCapacidad()))
																&& ((adjacencyMatrix[cd][cd] + capture) <= (150)) )
														{
                                adjacencyMatrix[i][j] = capture;
                                propagateThroughput(adjacencyMatrix[j][cd], sensors.get(j- ncent).getCapacidad()*3, capture, cd, j );
                                adjacencyMatrix[i][i] = j;
                                break;
                            }
                        }
                        dist[i][j] = distance(src_X, src_Y, dst_X, dst_Y);
                    }
                }
            }
        }
         print_map();
        initialSolution2_v2();
    }
		public void initialSolutionTestLoop(){
        int src_X, src_Y, dst_X,  dst_Y;
        for(int i = ncent; i < nElements -1; ++i) {
						src_X = sensors.get(i- ncent).getCoordX();
						src_Y = sensors.get(i- ncent).getCoordY();
						dst_X = sensors.get(i+1- ncent).getCoordX();
						dst_Y = sensors.get(i+1- ncent).getCoordY();
						dist[i][i+1] = distance(src_X, src_Y, dst_X, dst_Y);
						adjacencyMatrix[i][i+1] = sensors.get(i- ncent).getCapacidad();
						adjacencyMatrix[i][i] = i+1;
				}
				src_X = sensors.get(nElements -1- ncent).getCoordX();
				src_Y = sensors.get(nElements -1- ncent).getCoordY();
				dst_X = sensors.get(0).getCoordX();
				dst_Y = sensors.get(0).getCoordY();
				dist[nElements -1][ncent] = distance(src_X, src_Y, dst_X, dst_Y);
				adjacencyMatrix[nElements -1][ncent] = sensors.get(nElements -1- ncent).getCapacidad();
				adjacencyMatrix[nElements -1][nElements -1] = ncent;
				print_map();
		}


    public int[] getConnexions(){
        int [] copyConnections = new int[nsens];
        if (nsens >= 0) System.arraycopy(connexions, 0, copyConnections, 0, nsens);
        return copyConnections;

    }

    public double[] getThropt(){
        double [] copyThopt = new double[nsens];
        if (nsens >= 0) System.arraycopy(thropt, 0, copyThopt, 0, nsens);
        return copyThopt;

    }

    public double[] getDataDC(){
        double [] copyData = new double[ncent];
        if (ncent >= 0) System.arraycopy(dataDC, 0, copyData, 0, ncent);
        return copyData;
    }

    public int getNsens(){
        return nsens;
    }

    private void connect_v2(int i, int newConnection, double capture){
        connexions[i] = newConnection;
        if(newConnection >= ncent){
            propagateThroughput_v2(thropt[newConnection-ncent],
                    sensors.get(newConnection- ncent).getCapacidad()*3,
                    capture,
                    connexions[newConnection-ncent],
                    newConnection);
        }
        else dataDC[newConnection] += capture;
    }

    private void connect(int i, int newConnection, double capture){
        double c;
        adjacencyMatrix[i][newConnection] = capture;
        adjacencyMatrix[i][i] = newConnection;
        if(newConnection >= ncent){
            c = adjacencyMatrix[newConnection][newConnection];
            int cd = (int) c;
            propagateThroughput_v2(adjacencyMatrix[newConnection][cd],
                    sensors.get(newConnection- ncent).getCapacidad()*3,
                    capture,
                    cd,
                    newConnection);
        }
    }

    private void disconnect_v2(int i, int j){
        connexions[i] = -1;
        if(j >= ncent) {
            thropt[j-ncent] = dataVolume_v2(j);
            propagate_disconnect(connexions[j-ncent]);
        }
        else dataDC[j] = dataVolume_v2(j);
    }

    public void propagate_disconnect(int i){
        if (i >= ncent){
            thropt[i-ncent] = dataVolume_v2(i);
            propagate_disconnect(connexions[i-ncent]);
        }
        else dataDC[i] = dataVolume_v2(i);
    }

    public boolean canConnect(int j, int limit){
				ArrayList connections = getConnected(j);
        return connections.size() < limit;
    }

    public boolean canConnect_v2(int j, int limit){
        int count = 0;
           for(int i = 0; i < nsens; ++i){
               if(connexions[i] == j) ++count;
               if (count >= limit) return false;
           }
       return true;
    }

    public boolean findLoop_v2(int i, int j){
        if (j < ncent) return true;
        int next = connexions(j-ncent);
        if (next < ncent) return true;
        if (next == i) return false;
        return findLoop_v2(i,next);
    }

    public boolean findLoop(int i, int j){
			// i y j son nodos distintos
			// comprueba si i es antecesor de j (false) o no (true)
			int next = (int) adjacencyMatrix[j][j];
			if(next < ncent) return true;
			if(next == i) return false;
            return findLoop(i,next);
    }

    public double distance(int X1, int Y1, int X2, int Y2){
        return ((double) X1 - (double) X2)*((double) X1 - (double) X2)+((double) Y1 - (double) Y2)*((double) Y1 - (double) Y2);
    }

    public double getDistance(int i, int j){
        if (i < 0 || i >= nElements || j < 0 || j >= nElements) return -1;
        return dist[i][j];
    }

    public double getCost(int i, int j){
        if (i < 0 || i >= nElements || j < 0 || j >= nElements) return -1;
        return dist[i][j] * adjacencyMatrix[i][j];
    }

    public double getCost_v2(int i, int j){
        if (i < 0 || i >= nElements || j < 0 || j >= nElements) return -1;
        return dist[i][j] * thropt[i-ncent];
    }

    public void print_map_v2(){
        System.out.println();
        for (int i = 0; i < nsens; ++i) System.out.print(connexions[i]+" ");
        System.out.println();
        for (int j = 0; j < nsens; ++j) System.out.print(thropt[j] + " ");
        System.out.println();
        for (int j = 0; j < ncent; ++j) System.out.print(dataDC[j] + " ");

    }

    public void print_map(){
        for (int i = 0; i < nElements; ++i) {
            System.out.print("fila: " + i + " ");
            for (int j = 0; j < nElements; ++j)
                System.out.print(adjacencyMatrix[i][j] + " ");
            System.out.println();
        }
    }

    public ArrayList<Integer> getConnected(int node){
	    // returns an ArrayList with thorugput the nodes connected to "node"
			ArrayList<Integer> children = new ArrayList<>();
			for(int i = ncent; i < nElements; ++i){
				if((i != node) && (adjacencyMatrix[i][node] != 0)){
					children.add(i);
				}
			}
			return children;
    }

    public void newConnection_v2(int node, int oldConnection, int newConnection, double capture){
        disconnect_v2(node,oldConnection);
        connect_v2(node,newConnection,capture);
    }

    public void newConnection(int node, int oldConnection, int newConnection, double capture){
       disconnect(node,oldConnection);
       connect(node,newConnection,capture);
    }

    private void disconnect(int i, int j){
        double c;
        adjacencyMatrix[i][j] = 0;
        adjacencyMatrix[i][i] = 0;
        if(j >= ncent){
            c = adjacencyMatrix[j][j];
            int cd = (int) c;
            adjacencyMatrix[j][cd] = dataVolume(j);
        }
    }

    public double dataVolume(int j){
        double sum = 0;
        int num_connexions = 0;
        double max_throughput = 3*sensors.get(j-ncent).getCapacidad();
        for(int i = ncent; i < nElements; ++i){
            if (adjacencyMatrix[i][j] > 0){
                sum += adjacencyMatrix[i][j];
                ++num_connexions;
            }
            if(num_connexions >=3) return Math.min(sum, max_throughput);
        }
        return Math.min(sum, max_throughput);
    }

    public double dataVolume_v2(int j){
        double sum = 0;
        int num_connexions = 0;
        double max_throughput;
        int limit;
        if (j >= ncent) {
            max_throughput = 3*sensors.get(j-ncent).getCapacidad();
            limit = 3;
            if (connexions[j-ncent] != -1) sum += sensors.get(j-ncent).getCapacidad();
        }
        else {
            max_throughput = 150;
            limit = 25;
        }
        for(int i = 0; i < nsens; ++i){
            if (connexions[i] ==  j){
                sum += thropt[i];
                ++num_connexions;
            }
            if(num_connexions >=limit) return Math.min(sum, max_throughput);
        }
        return Math.min(sum, max_throughput);
    }

    public double adjacencyMatrix(int i, int j){return adjacencyMatrix[i][j];}

    public int connexions(int i){return connexions[i];}

    public double dataDC(int i){return dataDC[i];}

    public double dist(int i , int j){return dist[i][j];}

    public double thropt(int i){return thropt[i];}

    public double getCapacityOfSensor(int i){
        return sensors.get(i).getCapacidad()*3;
    }

    public int getNcent(){
        return (ncent);
		}

    public int getnElements(){
        return nElements;
    }

    public double[][] getDist() {
				return dist;
		}

    public double[][] getAdjacencyMatrix() {
        double[][] nadjacencyMatrix = new double[nElements][nElements];
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            System.arraycopy(adjacencyMatrix[i], 0, nadjacencyMatrix[i], 0, adjacencyMatrix.length);
        }
        return (nadjacencyMatrix);
    }

    public ArrayList<Centro> getDatacenters() {
				return datacenters;
		}

    public ArrayList<Sensor> getSensors() {
				return sensors;
		}
		public double getMaxData(){return maxData;}

    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder();
        for (int i = 0; i < nsens; ++i) retVal.append(connexions[i]).append(" ");
        retVal.append("\n");
        for (int i = 0; i < nsens; ++i) retVal.append(thropt[i]).append(" ");
        retVal.append("\n");
        for (int i = 0; i < ncent; ++i) retVal.append(dataDC[i]).append(" ");
        retVal.append("\n");
        return retVal.toString();

    }
}
