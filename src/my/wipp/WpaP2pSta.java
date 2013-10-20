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
    
    @Override
    public boolean equals(Object obj)
    {
        boolean ret = false;
        try
        {
            ret = (obj instanceof WpaP2pSta) && 
                    this.NAME.equals(((WpaP2pSta)obj).NAME)  &&
                    this.MAC_ADDR.equals(((WpaP2pSta)obj).MAC_ADDR) &&
                    this.Manufactor.equals(((WpaP2pSta)obj).Manufactor) &&
                    this.listen_freq == (((WpaP2pSta)obj).listen_freq);
        }
        catch(Exception ex){
            
        }
        return ret;
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
        if(Manufactor == null)
            this.Manufactor = "";
        else
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
