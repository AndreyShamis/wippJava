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
    public static BashResult RunCmd(String cmd) 
    {
        BashResult ret_res = new BashResult();
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
                if(ret_res.out.length()>0)
                    ret_res.out += "\n";
                ret_res.out += out.readLine();
            }
            ret_res.exitCode = proc.exitValue();
            while(err.ready())
                    ret_res.err += "\n" + err.readLine();

        }
        catch(IOException e)
        {
            ret_res.err += "\n" + "ERROR Exc:" + e.getMessage();
        } catch (InterruptedException ex) {
            ret_res.err += "\n" + "ERROR Exc:" + ex.getMessage();
            //AppendLog(ex.getMessage());
            Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ret_res;
    }    
}
