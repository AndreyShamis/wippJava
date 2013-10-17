/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.wipp;

/**
 *
 * @author andy
 */
public class WpaBssSta {
    protected String SSID;
    protected String MAC_ADDR;
    protected int Freq;
    protected int RSSI;

    /**
     * @return the SSID
     */
    public String getSSID() {
        return SSID;
    }

    /**
     * @param SSID the SSID to set
     */
    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    /**
     * @return the MAC_ADDR
     */
    public String getMAC_ADDR() {
        return MAC_ADDR;
    }

    /**
     * @param MAC_ADDR the MAC_ADDR to set
     */
    public void setMAC_ADDR(String MAC_ADDR) {
        this.MAC_ADDR = MAC_ADDR;
    }

    /**
     * @return the Freq
     */
    public int getFreq() {
        return Freq;
    }

    /**
     * @param Freq the Freq to set
     */
    public void setFreq(int Freq) {
        this.Freq = Freq;
    }

    /**
     * @return the RSSI
     */
    public int getRSSI() {
        return RSSI;
    }

    /**
     * @param RSSI the RSSI to set
     */
    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }
    
}
