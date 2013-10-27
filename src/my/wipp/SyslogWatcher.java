/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.wipp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tester
 */
public class SyslogWatcher implements Runnable{

    public StringBuilder log = new StringBuilder();
    @Override
    public void run() {
        String ret = "";
        // Location of file to read
        //File file = new File("tailf /tmp/wpaSupplicant.log");
 
        try {
            Process proc;
            proc = Runtime.getRuntime().exec("tailf /tmp/wpaSupplicant.log");
            BufferedReader out,err;
            out = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            //proc.waitFor();
            while (1==1){
                while(out.ready()){
                    String readl = out.readLine();
                    if(!readl.contains("RX ctrl_iface") && 
                            !readl.contains("Control interface command") && 
                            !readl.contains("SCAN_RESULTS") &&
                            !readl.contains("STATUS") &&
                            !readl.contains("P2P_PEER FIRST") &&
                            !readl.contains("SIGNAL_POLL"))
                    {
                        //System.out.println(readl);
                        log.append("\n" + readl);
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(SyslogWatcher.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                while(!out.ready()){
                    try {
                        Thread.sleep(3);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SyslogWatcher.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }

//            Scanner scanner = new Scanner(file);
// 
//            while (scanner.hasNextLine()) {
//                String line = scanner.nextLine();
//                System.out.println(line);
//            }
//            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(SyslogWatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
