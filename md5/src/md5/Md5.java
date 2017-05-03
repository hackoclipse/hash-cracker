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
import java.util.ArrayList;

/**
 *
 * @author mh123hack
 */
public class Md5 {

    /**
     * @param args the command line arguments
     */
    private static double klok = System.nanoTime();
    private static double time;
    private static double time2;
    private static ArrayList<String> arr = new ArrayList();
    private static String location = "";
    private static String hash = "";

//    private static String passworda = "123456";
    public static void main(String[] args) throws Exception {
        try {
            if (args[0].matches("[a-fA-F0-9]{32}") && !args[0].isEmpty()) {
                hash = args[0];
                location = args[1];
            } else if (args[1].matches("[a-fA-F0-9]{32}") && !args[1].isEmpty()) {
                hash = args[1];
                location = args[0];
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("faild to start change arguments to md5 hash, location of wordlist!");
            System.exit(0);
        }
        InputStream is;
        is = new FileInputStream(location);
        try (BufferedReader bfReader = new BufferedReader(new InputStreamReader(is))) {
            String sCurrentLine;
            while ((sCurrentLine = bfReader.readLine()) != null) {
                if (!sCurrentLine.equals("")) {
                    time = System.nanoTime();
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update(sCurrentLine.getBytes());
                    byte[] digest = md.digest();
                    StringBuffer sb = new StringBuffer();
                    for (byte b : digest) {
                        sb.append(String.format("%02x", b & 0xff));
                    }

                    if (hash.equals(sb.toString())) {
                        System.out.println(((double) System.nanoTime() - klok) / 1000000000);
                        System.out.println("hash = " + sb + " = " + sCurrentLine);
                        System.exit(0);
                    } else {
                        System.out.println("not the right password = " + sCurrentLine + " with hash = " + sb);
                    }
                    time2 = System.nanoTime();
                    System.out.println("hashes p/s = " + 1 / ((time2 - time) / 1000000000));
                }
            }
            System.out.println("faild to find password");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
