import java.util.*;
public class Main {
    public static void main(String[] args) throws Exception {
				int option = 2;
        RedSensorsState red = new RedSensorsState(2, 50, 1234, 4321, option);

				//tests
				boolean canConnectResult = true;
				for(int i = red.dataCenters(); i < red.size(); ++i){
					System.out.println("Main for loop:" + i);
					canConnectResult = canConnectResult && red.canConnect(i, 3);
					
				}
				if(canConnectResult) System.out.println(" se puede conectar en todos nodo");
				System.out.println("no se puede conectar en todos los nodos");


    }
}

//Sol inicial 1: es posible que se pierda informacion
//Sol inicial 2: se maximiza la información transmitida (no se pierde info, si es posible)
//Sol inicial 3: test para la funcion findLoop
