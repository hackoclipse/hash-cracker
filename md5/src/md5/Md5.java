/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package md5;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author mh123hack
 */
public class Md5 {

    /**
     * @param args the command line arguments
     */
    private static double klok = System.nanoTime();
    // location of wordlist.
    private static String location = "";
    // hash it has to find.
    private static String hash = "";
    private static String sCurrentLine;
    private static double testtijd;

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

        // here we chose witch file is used 
        InputStream is;
        is = new FileInputStream(location);
        try (BufferedReader bfReader = new BufferedReader(new InputStreamReader(is))) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            testtijd = System.nanoTime();
            while ((sCurrentLine = bfReader.readLine()) != null) {
                // this line is needed because the wordlist could have a empty row and it will generate a empty hash wich kost extra time.;
                if (!sCurrentLine.equals("")) {
                    // here we generate the hash and place a timer to see how fast in nanoseconds it take to create the hash.
                    // and we calculate how much hashes are made in 1 sec.
                    md.update(sCurrentLine.getBytes());
                    byte[] digest = md.digest();
                    StringBuffer sb = new StringBuffer();
                    for (byte b : digest) {
                        sb.append(String.format("%02x", b & 0xff));
                    }

                    // here we check if the hash is the same as the hash we have generated.
                    // if the hash is the same it will show the hash with the right combination and quits, else it says it isn't right and will keep going until the wordlist is empty.
                    if (hash.equals(sb.toString())) {
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
}
