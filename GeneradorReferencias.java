import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Actividad 1 - Caso 2 (ISIS 2203).
 * Genera las referencias (dvs) del algoritmo Hill Modificado sobre m[NF][NC]
 * (row-major) seguida en memoria por el vector clave v[TV].
 *
 * Uso: java GeneradorReferencias NF NC TV TP numPasadas archivoSalida
 * Sin argumentos, los parametros se piden por consola.
 */
public class GeneradorReferencias {

    /** Fin de linea estilo Windows, igual que el archivo de ejemplo. */
    private static final String FIN = "\r\n";

    private final int nf, nc, tv, tp, numPasadas;

    public GeneradorReferencias(int nf, int nc, int tv, int tp, int numPasadas) {
        this.nf = nf; this.nc = nc; this.tv = tv; this.tp = tp; this.numPasadas = numPasadas;
    }

    /** m empieza en la direccion 0 y se guarda por filas. */
    private int dirMatriz(int i, int j) { return i * nc + j; }

    /** v va justo despues de la matriz. */
    private int dirVector(int k) { return nf * nc + k; }

    public int numPaginas() { return (nf * nc + tv + tp - 1) / tp; }

    /** 3 referencias por elemento, 2 recorridos por pasada. */
    public long numReferencias() { return 3L * nf * nc * 2 * numPasadas; }

    private void escribir(BufferedWriter out, String etiqueta, int dv) throws IOException {
        out.write(etiqueta + "," + (dv / tp) + "," + (dv % tp));
        out.write(FIN);
    }

    /** m[i][j] = m[i][j] op v[k] -> lee m, lee v, escribe m. */
    private void operacion(BufferedWriter out, int i, int j, int k) throws IOException {
        String m = "[mat1-" + i + "-" + j + "]";
        escribir(out, m, dirMatriz(i, j));
        escribir(out, "[v-0-" + k + "]", dirVector(k));
        escribir(out, m, dirMatriz(i, j));
    }

    public void generar(String archivo) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(archivo), StandardCharsets.UTF_8), 1 << 16)) {
            out.write("TP=" + tp); out.write(FIN);
            out.write("NF=" + nf); out.write(FIN);
            out.write("NC=" + nc); out.write(FIN);
            out.write("Tamaño vector clave=" + tv); out.write(FIN);
            out.write("numPasadas=" + numPasadas); out.write(FIN);
            out.write("NR=" + numReferencias()); out.write(FIN);
            out.write("NP=" + numPaginas()); out.write(FIN);

            for (int pasada = 0; pasada < numPasadas; pasada++) {
                // Recorrido por filas: suma con v[j % TV]
                for (int i = 0; i < nf; i++)
                    for (int j = 0; j < nc; j++)
                        operacion(out, i, j, j % tv);
                // Recorrido por columnas: XOR con v[i % TV]
                for (int j = 0; j < nc; j++)
                    for (int i = 0; i < nf; i++)
                        operacion(out, i, j, i % tv);
            }
        }
    }

    private static int leerEntero(Scanner sc, String msg) {
        System.out.print(msg);
        return Integer.parseInt(sc.nextLine().trim());
    }

    public static void main(String[] args) throws IOException {
        int nf, nc, tv, tp, pasadas;
        String archivo;
        if (args.length == 6) {
            nf = Integer.parseInt(args[0]); nc = Integer.parseInt(args[1]);
            tv = Integer.parseInt(args[2]); tp = Integer.parseInt(args[3]);
            pasadas = Integer.parseInt(args[4]); archivo = args[5];
        } else {
            Scanner sc = new Scanner(System.in);
            nf = leerEntero(sc, "Numero de filas (NF): ");
            nc = leerEntero(sc, "Numero de columnas (NC): ");
            tv = leerEntero(sc, "Tamano del vector clave: ");
            tp = leerEntero(sc, "Tamano de pagina (bytes): ");
            pasadas = leerEntero(sc, "Numero de pasadas: ");
            System.out.print("Archivo de salida: ");
            archivo = sc.nextLine().trim();
        }
        if (nf <= 0 || nc <= 0 || tv <= 0 || tp <= 0 || pasadas < 0) {
            System.err.println("Parametros invalidos.");
            System.exit(1);
        }
        GeneradorReferencias gen = new GeneradorReferencias(nf, nc, tv, tp, pasadas);
        gen.generar(archivo);
        System.out.println("Generado " + archivo + " (NR=" + gen.numReferencias()
                + ", NP=" + gen.numPaginas() + ")");
    }
}
