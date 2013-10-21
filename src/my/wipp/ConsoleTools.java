/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.wipp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tester
 */
public class ConsoleTools {
    public static String RunCmd(String cmd) 
    {
        String ret = "";
        try
        {
            Process proc;
            proc = Runtime.getRuntime().exec(cmd);
            BufferedReader out,err;
            out = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            proc.waitFor();
            while(out.ready()){
                //AppendLog(out.readLine());
                if(ret.length()>0)
                    ret += "\n";
                ret += out.readLine();
            }
            if(proc.exitValue() != 0 && proc.exitValue() != 255)
            {
                //AppendLog("[" + cmd + "]Exit code = " + proc.exitValue() + " " +ret);
                ret += "Error:[" + cmd + "]Exit code = " + proc.exitValue() + " " +ret;
                String errStr = err.readLine();
                
                if(errStr != null)
                    ret += "Error: errStr";
//                    AppendLog(errStr);
            }
        }
        catch(IOException e)
        {
            ret += "ERROR Exc:" + e.getMessage();
        } catch (InterruptedException ex) {
            ret += "ERROR Exc:" + ex.getMessage();
            //AppendLog(ex.getMessage());
            Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ret;
    }    
}
