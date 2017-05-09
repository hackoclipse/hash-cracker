package md5;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;

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

//    private static String passworda = "123456";
    public static void main(String[] args) throws Exception {
        // in this part we check if the first argument or the second argument the hash is.
        // if the hash is as second argument it will place this in de hash variable.
        try {
            if (args[0].matches("[a-fA-F0-9]{32}") && !args[0].isEmpty()) {
                hash = args[0];
                location = args[1];
            } else if (args[1].matches("[a-fA-F0-9]{32}") && !args[1].isEmpty()) {
                hash = args[1];
                location = args[0];
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // still a error i am working on if you give 3 arguments instead of 2.
            System.err.println("faild to start change arguments to md5 hash, location of wordlist!");
            System.exit(0);
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
            MessageDigest md = MessageDigest.getInstance("MD5");
            while ((sCurrentLine = bfReader.readLine()) != null) {
                // this line is needed because the wordlist could have a empty row and it will generate a empty hash wich kost extra time.;
                if (!sCurrentLine.equals("")) {
                    // here we generate the hash.
                    md.update(sCurrentLine.getBytes());
                    byte[] digest = md.digest();

                    // here we check if the hash is the same as the hash we have generated.
                    // if the hash is the same it will show the hash with the right combination and quits, else it says it isn't right and will keep going until the wordlist is empty.
                    if (ComperByteArray(hashb, digest)) {
                        StringBuffer sb = new StringBuffer();
                        for (byte b : digest) {
                            sb.append(lookuptable[b & 0xff]);
                        }
                        System.out.println("hash = " + sb + " = " + sCurrentLine);
                        System.exit(0);
                    }
                }
            }
            System.out.println("faild to find password"); //  if all the combinations aren't right it will print "faild to find password".

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // here we compere 2 byte arrays to see if thay are the same.
    public static boolean ComperByteArray(byte[] a, byte[] b) {
        if(a.length != b.length){
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if(a[i]!= b[i]){
                return false;
            }
        }
        return true;
    }
}
