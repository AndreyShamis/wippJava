/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.wipp;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tester
 */
public class BssConnect implements Runnable{

    private String cmd_Connect = "";
    private StringBuilder log = new StringBuilder();
    @Override
    public void run() {
        try {
            System.out.println("Start runnable");
            log.append(ConsoleTools.RunCmd(cmd_Connect));
            System.out.println("Sleep before request IP");
            Thread.sleep(4000);
            System.out.println("Start request IP");
            DhcpRequest();
            System.out.println("Finish request IP");
        } catch (InterruptedException ex) {
            Logger.getLogger(BssConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void DhcpRequest()
    {
        log.append(ConsoleTools.RunCmd("./Scripts/dhcp-client.sh"));
    }
    
    /**
     * @return the cmd_Connect
     */
    public String getCmd_Connect() {
        return cmd_Connect;
    }

    /**
     * @param cmd_Connect the cmd_Connect to set
     */
    public void setCmd_Connect(String cmd_Connect) {
        this.cmd_Connect = cmd_Connect;
    }

    /**
     * @return the log
     */
    public StringBuilder getLog() {
        return log;
    }

    /**
     * @param log the log to set
     */
    public void setLog(StringBuilder log) {
        this.log = log;
    }
    
}
