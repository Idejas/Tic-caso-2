import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

// Caso 2 - Actividad 1
// Genera el archivo con las direcciones virtuales que produce el metodo cifrar()
// La matriz esta guardada por filas desde la direccion 0 y el vector va justo despues
public class GeneradorReferencias {

    static int filas, columnas, tamVector, tamPagina, pasadas;
    static BufferedWriter bw;

    public static void main(String[] args) throws IOException {
        String nombreArchivo;

        if (args.length == 6) {
            filas = Integer.parseInt(args[0]);
            columnas = Integer.parseInt(args[1]);
            tamVector = Integer.parseInt(args[2]);
            tamPagina = Integer.parseInt(args[3]);
            pasadas = Integer.parseInt(args[4]);
            nombreArchivo = args[5];
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.print("Filas de la matriz: ");
            filas = sc.nextInt();
            System.out.print("Columnas de la matriz: ");
            columnas = sc.nextInt();
            System.out.print("Tamanio del vector: ");
            tamVector = sc.nextInt();
            System.out.print("Tamanio de pagina: ");
            tamPagina = sc.nextInt();
            System.out.print("Numero de pasadas: ");
            pasadas = sc.nextInt();
            System.out.print("Nombre del archivo de salida: ");
            nombreArchivo = sc.next();
            sc.close();
        }

        // 3 referencias por cada elemento (leer m, leer v, escribir m) y 2 recorridos por pasada
        long numReferencias = (long) filas * columnas * 3 * 2 * pasadas;
        int totalBytes = filas * columnas + tamVector;
        int numPaginas = totalBytes / tamPagina;
        if (totalBytes % tamPagina != 0) {
            numPaginas++;
        }

        bw = new BufferedWriter(new FileWriter(nombreArchivo, StandardCharsets.UTF_8));
        escribirLinea("TP=" + tamPagina);
        escribirLinea("NF=" + filas);
        escribirLinea("NC=" + columnas);
        escribirLinea("Tamaño vector clave=" + tamVector);
        escribirLinea("numPasadas=" + pasadas);
        escribirLinea("NR=" + numReferencias);
        escribirLinea("NP=" + numPaginas);

        for (int p = 0; p < pasadas; p++) {
            // recorrido por filas (suma)
            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    referencias(i, j, j % tamVector);
                }
            }
            // recorrido por columnas (xor)
            for (int j = 0; j < columnas; j++) {
                for (int i = 0; i < filas; i++) {
                    referencias(i, j, i % tamVector);
                }
            }
        }
        bw.close();

        System.out.println("Listo, se genero " + nombreArchivo);
        System.out.println("NR = " + numReferencias + "  NP = " + numPaginas);
    }

    // m[i][j] = m[i][j] op v[k]  ->  lee m[i][j], lee v[k], escribe m[i][j]
    static void referencias(int i, int j, int k) throws IOException {
        int dirM = i * columnas + j;
        int dirV = filas * columnas + k;
        String m = "[mat1-" + i + "-" + j + "]," + dirM / tamPagina + "," + dirM % tamPagina;
        escribirLinea(m);
        escribirLinea("[v-0-" + k + "]," + dirV / tamPagina + "," + dirV % tamPagina);
        escribirLinea(m);
    }

    // se usa \r\n para que quede igual al archivo de ejemplo del enunciado
    static void escribirLinea(String linea) throws IOException {
        bw.write(linea + "\r\n");
    }
}
