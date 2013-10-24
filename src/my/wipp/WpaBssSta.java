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

    private String SSID = "";
    private String MAC_ADDR = "";
    private int Freq = 0;
    private int RSSI = 0;
    private String Security = "";
    private String Chiper = "";
    private String Password = "";
    private String WpaState = "";
    private int LinkSpeed = 0;
    private String Width = "";
    private int Avg_RSSI = 0;

    public WpaBssSta(){
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof WpaBssSta)
                && this.SSID.equals(((WpaBssSta) obj).SSID)
                && this.MAC_ADDR.equals(((WpaBssSta) obj).MAC_ADDR)
                && this.getSecurity().equals(((WpaBssSta) obj).getSecurity())
                && this.getChiper().equals(((WpaBssSta) obj).getChiper())
                && this.Freq == (((WpaBssSta) obj).Freq)
                && this.RSSI == (((WpaBssSta) obj).RSSI);
    }

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

    /**
     * @return the Security
     */
    public String getSecurity() {
        return Security;
    }

    /**
     * @param Security the Security to set
     */
    public void setSecurity(String Security) {
        this.Security = Security;
    }

    /**
     * @return the Chiper
     */
    public String getChiper() {
        return Chiper;
    }

    /**
     * @param Chiper the Chiper to set
     */
    public void setChiper(String Chiper) {
        this.Chiper = Chiper;
    }

    /**
     * @return the Password
     */
    public String getPassword() {
        return Password;
    }

    /**
     * @param Password the Password to set
     */
    public void setPassword(String Password) {
        this.Password = Password;
    }

    /**
     * @return the WpaState
     */
    public String getWpaState() {
        return WpaState;
    }

    /**
     * @param WpaState the WpaState to set
     */
    public void setWpaState(String WpaState) {
        this.WpaState = WpaState;
    }

    /**
     * @return the LinkSpeed
     */
    public int getLinkSpeed() {
        return LinkSpeed;
    }

    /**
     * @param LinkSpeed the LinkSpeed to set
     */
    public void setLinkSpeed(int LinkSpeed) {
        this.LinkSpeed = LinkSpeed;
    }

    /**
     * @return the Width
     */
    public String getWidth() {
        return Width;
    }

    /**
     * @param Width the Width to set
     */
    public void setWidth(String Width) {
        this.Width = Width;
    }

    /**
     * @return the Avg_RSSI
     */
    public int getAvg_RSSI() {
        return Avg_RSSI;
    }

    /**
     * @param Avg_RSSI the Avg_RSSI to set
     */
    public void setAvg_RSSI(int Avg_RSSI) {
        this.Avg_RSSI = Avg_RSSI;
    }

}
