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
    
    @Override
    public void run() {
        try {
            System.out.println("Start runnable");
            ConsoleTools.RunCmd(cmd_Connect);
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
        ConsoleTools.RunCmd("./Scripts/dhcp-client.sh");
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
    
}
