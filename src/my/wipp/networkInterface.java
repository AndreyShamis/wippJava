/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.wipp;

/**
 *
 * @author werd
 */
public class networkInterface {
    private String MAC_ADDR;
    private String NAME;
    private String IP_ADDR;    

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
     * @return the IP_ADDR
     */
    public String getIP_ADDR() {
        return IP_ADDR;
    }

    /**
     * @param IP_ADDR the IP_ADDR to set
     */
    public void setIP_ADDR(String IP_ADDR) {
        this.IP_ADDR = IP_ADDR;
    }
}
