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
public class WpaP2pSta {
    protected String MAC_ADDR;
    protected String NAME;
    protected String Manufactor;
    protected int listen_freq;

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
     * @return the NAME
     */
    public String getNAME() {
        return NAME;
    }

    /**
     * @param NAME the NAME to set
     */
    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    /**
     * @return the Manufactor
     */
    public String getManufactor() {
        return Manufactor;
    }

    /**
     * @param Manufactor the Manufactor to set
     */
    public void setManufactor(String Manufactor) {
        this.Manufactor = Manufactor;
    }

    /**
     * @return the listen_freq
     */
    public int getListen_freq() {
        return listen_freq;
    }

    /**
     * @param listen_freq the listen_freq to set
     */
    public void setListen_freq(int listen_freq) {
        this.listen_freq = listen_freq;
    }
}
