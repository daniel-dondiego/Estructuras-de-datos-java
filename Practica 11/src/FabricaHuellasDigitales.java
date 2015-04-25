package mx.unam.ciencias.edd;

/**
 * Clase para fabricar generadores de huellas digitales.
 */
public class FabricaHuellasDigitales {

    private static class BobJenkins implements HuellaDigital<String> {
        public int huellaDigital(String cad) {
            int  h = (int) hash(cad.getBytes());
            return h;
        }
         long a,b,c;

        private int hash(byte [] arr) {
            int opcion = arr.length;
            a = 0x000000009e3779b9L;
            b = a;
            c = 0xffffffff;
            int i = 0;

            while (opcion >= 12) {
                a += (arr[i] + (arr[i+1] << 8) + (arr[i+2] << 16) + (arr[i+3] << 24));
                b += (arr[i+4] + (arr[i+5] << 8) + (arr[i+6] << 16) + (arr[i+7] << 24));
                c += (arr[i+8] + (arr[i+9] << 8) + (arr[i+10] << 16) + (arr[i+11] << 24));
                mezcla();
                i += 12;
                opcion -= 12;
            }
            c += arr.length;

            switch(opcion) {
                case 11:
                c = add(c, leftShift(byteLong(arr[i + 10]), 24));
                case 10:
                    c = add(c, leftShift(byteLong(arr[i + 9]), 16));
                case 9:
                    c = add(c, leftShift(byteLong(arr[i + 8]), 8));
                case 8:
                    b = add(b, leftShift(byteLong(arr[i + 7]), 24));
                case 7:
                    b = add(b, leftShift(byteLong(arr[i + 6]), 16));
                case 6:
                    b = add(b, leftShift(byteLong(arr[i + 5]), 8));
                case 5:
                    b = add(b, (arr[i + 4]));
                case 4:
                    a = add(a, leftShift(byteLong(arr[i + 3]), 24));
                case 3:
                    a = add(a, leftShift(byteLong(arr[i + 2]), 16));
                case 2:
                    a = add(a, leftShift(byteLong(arr[i + 1]), 8));
                case 1:
                    a = add(a, (arr[i + 0]));
            }
            mezcla();
            return (int) (c&0xffffffff);
        }

        private long byteLong(byte b) {
            long val = b & 0x7F;
             return val;
        }

        private void mezcla() {
           a = resta(a, b); a = resta(a, c); a = xor(a, c >> 13);
           b = resta(b, c); b = resta(b, a); b = xor(b, leftShift(a, 8));
           c = resta(c, a); c = resta(c, b); c = xor(c, (b >> 13));
           a = resta(a, b); a = resta(a, c); a = xor(a, (c >> 12));
           b = resta(b, c); b = resta(b, a); b = xor(b, leftShift(a, 16));
           c = resta(c, a); c = resta(c, b); c = xor(c, (b >> 5));
           a = resta(a, b); a = resta(a, c); a = xor(a, (c >> 3));
           b = resta(b, c); b = resta(b, a); b = xor(b, leftShift(a, 10));
           c = resta(c, a); c = resta(c, b); c = xor(c, (b >> 15));

        }

        private long leftShift(long n, int r) {
            return (n << r) & 0x00000000ffffffffL;
        }

        private long resta(long a, long b) {
            return (a - b) & 0x00000000ffffffffL;
        }

        private long xor(long a, long b) {
            return (a ^ b) & 0x00000000ffffffffL;
        }

        private long add(long a, long b) {
            return (a + b) & 0x00000000ffffffffL;
        }

  
    }

    /**
     * Identificador para fabricar la huella digital de Bob
     * Jenkins para cadenas.
     */
    public static final int BJ_STRING   = 0;
    /**
     * Identificador para fabricar la huella digital de GLib para
     * cadenas.
     */
    public static final int GLIB_STRING = 1;
    /**
     * Identificador para fabricar la huella digital de XOR para
     * cadenas.
     */
    public static final int XOR_STRING  = 2;



    /**
     * Regresa una instancia de {@link HuellaDigital} para cadenas.
     * @param identificador el identificador del tipo de huella
     *        digital que se desea.
     * @throws IllegalArgumentException si recibe un identificador
     *         no reconocido.
     */
    public static HuellaDigital<String> getInstanciaString(int identificador) {
        if(identificador < 0|| identificador > 2)
            throw new IllegalArgumentException();
        switch (identificador) {
            
            case 1 : return new HuellaDigital<String>() {
                public int huellaDigital(String cad) {
                    byte[] arr = cad.getBytes();
                    int num = 5381;
                    char ch;
                    for(int i = 0; i < arr.length; i++) {
                        ch = (char)arr[i];
                        num = num*33 + ch;}   
                    return num;}};
            case 2 : return new HuellaDigital<String>() {
                public int huellaDigital(String cad) {
                    byte[] arr = cad.getBytes();
                    int x,y,val; 
                    x = arr.length;
                    y = arr.length;
                    val = 0;
                    if((y&3) != 0)
                    x = y + 4 - (y&3);
                    byte[] n = new byte[x];
                    for(int i = x-y; i < x; i++)
                        n[i] = arr[i-(x-y)];
                    for(int j = 0; j < x; j+=4) {
                        int a1 = (int)n[j] << 24;
                        int a2 = (int)n[j+1] << 16;
                        int a3 = (int)n[j+2] << 8;
                        int a4 = (int)n[j+3];
                        int b = a1|a2|a3|a4;
                        val ^= b;
                    }
                    return val;}};
            case 0 : //BJ
            return new BobJenkins();
        }
        return null;
    }
}
