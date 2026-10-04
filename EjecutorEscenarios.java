import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

// Caso 2 - Actividad 2
// Corre el simulador para todos los escenarios pedidos (3 matrices x 3 paginas x 3 marcos x 3 politicas = 81)
// y guarda los resultados en resultados.csv para pasarlos al Excel
public class EjecutorEscenarios {

    // estos dos no los da el enunciado, usamos los mismos del archivo de ejemplo
    static final int TAM_VECTOR = 142;
    static final int PASADAS = 5;

    static int[] matrices = {8, 16, 128};
    static int[] paginas = {64, 256, 1024};
    static int[] marcos = {4, 8, 16};
    static String[] politicas = {"FIFO", "FIFOModified", "LRU"};

    public static void main(String[] args) throws Exception {
        new File("referencias").mkdir();
        PrintWriter csv = new PrintWriter(new FileWriter("resultados.csv"));
        csv.println("Matriz,TP,NP,Marcos,Politica,NR,Fallas,Exitos,TasaFallas");

        for (int n : matrices) {
            for (int tp : paginas) {
                // primero se genera el archivo de referencias de la actividad 1
                String archivo = "referencias/ref_" + n + "x" + n + "_tp" + tp + ".txt";
                GeneradorReferencias.main(new String[]{"" + n, "" + n, "" + TAM_VECTOR, "" + tp, "" + PASADAS, archivo});

                long nr = (long) n * n * 3 * 2 * PASADAS;
                int np = (n * n + TAM_VECTOR + tp - 1) / tp;

                for (int m : marcos) {
                    for (String pol : politicas) {
                        long fallas = correrSimulador(archivo, m, pol);
                        long exitos = nr - fallas;
                        double tasa = (double) fallas / nr;
                        csv.println(n + "x" + n + "," + tp + "," + np + "," + m + "," + pol + ","
                                + nr + "," + fallas + "," + exitos + "," + tasa);
                        System.out.println(n + "x" + n + " TP=" + tp + " marcos=" + m + " " + pol + " -> fallas=" + fallas);
                    }
                }
            }
        }
        csv.close();
        System.out.println("Resultados en resultados.csv");
    }

    // ejecuta el simulador.jar y saca el total de fallos de la ultima parte de la salida
    // la linea que nos interesa es: "********Proceso 1 Total accesos X Total fallos Y"
    static long correrSimulador(String archivo, int numMarcos, String politica) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "simulador.jar", archivo, "" + numMarcos, politica);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        long fallos = -1;
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String linea;
        while ((linea = br.readLine()) != null) {
            if (linea.contains("Total fallos")) {
                String[] partes = linea.trim().split(" ");
                fallos = Long.parseLong(partes[partes.length - 1]);
            }
        }
        p.waitFor();
        return fallos;
    }
}
