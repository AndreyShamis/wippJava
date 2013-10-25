/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.wipp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tester
 */
public class DmesgWatcher implements Runnable{
    public StringBuilder log = new StringBuilder();
    
    
    private void StartPrintDMesgToFile()
    {

    }
    
    @Override
    public void run() {
        String ret = "";
        // Location of file to read
        //File file = new File("tailf /tmp/wpaSupplicant.log");
 
        try {
            new Thread(new Runnable() {

                @Override
                public void run() {
                        Process proce;
                        BufferedReader out,err = null;
                        try {
                            log.append("\n Start Watcher");
                            proce = Runtime.getRuntime().exec("./Scripts/dmesgWatch.sh");
                   
                            out = new BufferedReader(new InputStreamReader(proce.getInputStream()));
                            err = new BufferedReader(new InputStreamReader(proce.getErrorStream()));
                            try {
                                proce.waitFor();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(DmesgWatcher.class.getName()).log(Level.SEVERE, null, ex);
                                log.append(DmesgWatcher.class.getName() + ex.getMessage());
                            }
                        } catch (IOException ex) {
                            log.append(DmesgWatcher.class.getName() + ex.getMessage());
                            Logger.getLogger(DmesgWatcher.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    try {
                        log.append("\n Watcher Ended " + err.readLine() + err.readLine());
                    } catch (IOException ex) {
                        Logger.getLogger(DmesgWatcher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    }
            }).start();
            
            Process proc;
            proc = Runtime.getRuntime().exec("tailf ./dmesg.log");
            BufferedReader out,err;
            out = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            //proc.waitFor();
            while (1==1){
                while(out.ready()){
                    String readl = out.readLine();
                    if(readl.contains("iwl") || 
                            readl.contains("wpa") ||
                            readl.contains("80211") ||
                            readl.contains("cfg")
                            )
                    {
                        System.out.println(readl);
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
