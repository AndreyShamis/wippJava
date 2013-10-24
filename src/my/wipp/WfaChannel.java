/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.wipp;

/**
 *
 * @author tester
 */
public class WfaChannel {
    private int value;
    public static int FrequencyToChannel(int Freq){
        int ret = 0;
        if(Freq >=2412 && Freq < 2472){
            ret = ((Freq - 2412)/5 ) + 1;
        }else if(Freq == 2484){
            ret = 14;
        }else if(Freq >=5170 && Freq <=5825){
            ret = ((Freq - 5170)/5 ) + 34;
        }
        return ret;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
    }
}
