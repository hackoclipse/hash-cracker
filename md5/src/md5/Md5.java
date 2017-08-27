package md5;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mh123hack
 */
public class Md5 {

    /**
     * @param args the command line arguments
     */
    // timer to see how much hashes it makes in 1 sec.
    // location of wordlist.
    private static String location = "";
    // hash it has to find.
    private static String hash;
    private static String sCurrentLine;
    private static long time;

    private static final int THREAD_COUNT = 6;  // play with this number to optimize speed, depends on computer. 
    public static String[] pwds = new String[THREAD_COUNT];
    public static int[] statusT2M = new int[THREAD_COUNT]; // Thread to master
    public static int[] statusM2T = new int[THREAD_COUNT];
    public static final int T2M_I_FOUND_IT = 10;
    public static final int T2M_I_WANT_NEXT = 15;
    public static final int T2M_I_AM_BUSY = 11;
    public static final int M2T_BEGIN_HASHING = 4;
    public static final int M2T_WAIT = 7; // tread must ait for master to put password
    private static workerthread myworkers[] = new workerthread[THREAD_COUNT];


   // usage <exename> MD5hash filename    
   // or    
   // usage <exename> filename MD5hash     
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < THREAD_COUNT; i++) {
            statusT2M[i] = T2M_I_AM_BUSY;
            statusM2T[i] = M2T_WAIT;
        }

        // in this part we check if the first argument or the second argument the hash is.
        // if the hash is as second argument it will place this in de hash variable.
        try {                             
            if (args[0].matches("[a-fA-F0-9]{32}")) {
                hash = args[0];
                location = args[1];
            } else if (args[1].matches("[a-fA-F0-9]{32}")) {
                hash = args[1];
                location = args[0];
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // still a error i am working on if you give 3 arguments instead of 2.
            System.err.println("faild to start change arguments to md5 hash, location of wordlist!");
            System.exit(0);
        }
        if (hash==null || hash.length()!=32 || location==null || location.length()<1) {
            System.err.println("Recover password from MD5Hash using a password list.\nUsage:\tHashCracker MD5hash passwordlist    \n" + "or \tHashCracker passwordlist  MD5hash    ");
            System.exit(1);
        }
        // zet een md5 hash string om in een array met bytes.
        byte[] hashb = new byte[hash.length() / 2];
        char[] hashchar = hash.toCharArray();
        for (int i = 0; i < hashchar.length; i = i + 2) {
            int leftchar = (int) hashchar[i] - (int) '0';
            int rightchar = (int) hashchar[i + 1] - (int) '0';
            if (leftchar > 9) {
                leftchar = leftchar - 39;
            }
            if (rightchar > 9) {
                rightchar = rightchar - 39;
            }
            hashb[i / 2] = (byte) (leftchar * 16 + rightchar);
        }
        // look up the string in a list.
        String[] lookuptable = new String[256];
        for (int i = 0; i < 255; i++) {
            lookuptable[i] = String.format("%02x", i);
        }

        // here we chose witch file is used 
        InputStream is;
        is = new FileInputStream(location);
        try (BufferedReader bfReader = new BufferedReader(new InputStreamReader(is))) {
            //MessageDigest md = MessageDigest.getInstance("MD5");
            time = System.currentTimeMillis();

            for (int i = 0; i < THREAD_COUNT; i++) { // is er een thread uit zijn neus aan het peuteren?
                //        workerthread(String threadName, byte[] gezochte_md5, String eenwoorduitlijst) {
                String s = "Mijn draadje + " + i;
                myworkers[i] = new workerthread();
                myworkers[i].start_the_thread(s, hashb, i);
            }

            long linecounter = 0;
            long lc = 0;
            // threads aan het werk zetten
            while (true) {
                for (int i = 0; i < THREAD_COUNT; i++) { // is er een thread uit zijn neus aan het peuteren?
                    if (statusT2M[i] == T2M_I_WANT_NEXT) {
                        while ((sCurrentLine = bfReader.readLine()) != null) {
                            // this line is needed because the wordlist could have a empty row and it will generate a empty hash wich kost extra time.;
                            if (sCurrentLine.length() > 0) {
                                pwds[i] = sCurrentLine;
                                statusM2T[i] = 0;
                                linecounter++;
//System.out.flush();

                                if (++lc >= 500000) {
                                    lc = 0;
                                    System.out.println("try[" + linecounter + "] = " + sCurrentLine);
                                }

                                break; // uit while
                            }
                        }
                        if (sCurrentLine == null) { // file empty
                            int j = 0;
                            for (; j < THREAD_COUNT; j++) { // is er een thread uit zijn neus aan het peuteren?
                                if (statusT2M[j] != T2M_I_WANT_NEXT) {
                                    break;
                                }
                            }
                            if (j >= THREAD_COUNT) {
                                System.out.println("failed to find password");
                                System.exit(0);
                            }
                        }
                    }
                    if (statusT2M[i] == T2M_I_AM_BUSY) {
                       statusM2T[i]=M2T_WAIT;
                    }
                    if (statusT2M[i] == T2M_I_FOUND_IT) {
                        System.out.println("time it took = " + (System.currentTimeMillis() - time));
                        System.out.println("password = " + pwds[i] + " form hash " + hash); //  if all the combinations aren't right it will print "faild to find password".
                        // threads closing
                        System.exit(0);
                    }
                }
                Thread.currentThread().yield();
            }
            
        }
    }

    public static boolean ComperByteArray(byte[] a, byte[] b) {
        // here we compere 2 byte arrays to see if thay are the same.
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }
}