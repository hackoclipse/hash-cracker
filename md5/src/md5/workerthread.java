/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package md5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static md5.Md5.ComperByteArray;
import static md5.Md5.pwds;
import static md5.Md5.statusM2T;
import static md5.Md5.statusT2M;
import static md5.Md5.T2M_I_FOUND_IT;
import static md5.Md5.T2M_I_WANT_NEXT;

/**
 * https://www.tutorialspoint.com/java/java_multithreading.htm
 *
 * @author mh123hack
 * The Runnable interface should be implemented by any class whose instances are intended to be executed by a thread. 
 * The class must define a method of no arguments called run.
 */
class workerthread implements Runnable {
    private Thread t;
    private byte[] myhash;
    private String mythreadname;
    private int myindexwoordinpwds;

    
    // constructor of this class object
    
    workerthread() {
    }

    // save some values to be used in thread when started// 
    public Thread start_the_thread(String threadName, byte[] gezochte_md5, int indexwoordinpwds) {
//        System.out.println("Starting " + mythreadname);
       mythreadname = threadName;
        myhash = gezochte_md5;
        myindexwoordinpwds = indexwoordinpwds;
         if (t == null) {  // security to start only once
            t = new Thread(this, "MD5" + mythreadname);
            t.start();
        }
        return t; // to send to main program
    }

    public void run() {
        try {
            System.out.println("Running " + mythreadname);
            //try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            statusT2M[myindexwoordinpwds] = T2M_I_WANT_NEXT;
            while (true) {
                if (pwds[myindexwoordinpwds] != null && pwds[myindexwoordinpwds].length() > 0) {
                    statusT2M[myindexwoordinpwds] = Md5.T2M_I_AM_BUSY;

                    md.update(pwds[myindexwoordinpwds].getBytes());
                    byte[] digest = md.digest();
                    //pwds[myindexwoordinpwds] = "";
                    if (ComperByteArray(myhash, digest)) {
                        statusT2M[myindexwoordinpwds] = T2M_I_FOUND_IT;
// gevonden
// System.out.println("time it took = " + (System.currentTimeMillis() - time));
                        System.out.println("succesfol");
                        try {
                            Thread.currentThread().join();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(workerthread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        statusT2M[myindexwoordinpwds] = T2M_I_WANT_NEXT;
                    }
                } else {
                    Thread.currentThread().yield();
                }
            }

            //} catch (InterruptedException e) {
            //    System.out.println("Thread " + mythreadname + " interrupted.");
            //}
            //System.out.println("Thread " + mythreadname + " exiting.");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Md5.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
