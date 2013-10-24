/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package my.wipp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author andy
 */
public class frmMain extends javax.swing.JFrame {

    private ArrayList<WpaBssSta>        m_Bss           = new ArrayList<WpaBssSta>();
    private ArrayList<WpaP2pSta>        m_P2p           = new ArrayList<WpaP2pSta>();
    private ArrayList<networkInterface> m_Interfeces    = new ArrayList<networkInterface>();
    private String m_BSSInterfaceName   =   "wlan0";
    private String m_P2PInterfaceName   =   "p2p0";
    private boolean  m_Scaned = false;
    
    private WpaBssSta m_Self = new WpaBssSta();
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------

    
    ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            try{
                GUIUpdateNetworkInterfaces();
                GUIUpdateP2pPeers();
                GUIUpdateBss();
                if(m_Bss != null)
                    lblBssCount.setText(Integer.toString(m_Bss.size()));
                if(m_P2p != null)
                    lblP2pPeersCount.setText(Integer.toString(m_P2p.size()));
                if(m_Self != null){
                    lblRssi.setText(""+ m_Self.getRSSI());
                    lblBssWpaState.setText(m_Self.getWpaState());
                    lblBssSSID.setText(m_Self.getSSID());
                    lblBssWidth.setText(m_Self.getWidth());
                }
            }catch(Exception ex){
                AppendLog("Timer error:" + ex.getMessage() );
            }
        }
    };
    
    ActionListener timerActionSlow = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            try{

                UpdateBssStatus();
                UpdateBssSignalPoll();
            }catch(Exception ex){
                AppendLog("Timer error:" + ex.getMessage());
            }
        }
    };
    
    private final Timer timer  = new Timer(700,al);
    private final Timer timerSlow  = new Timer(1000,timerActionSlow);
    
    
    private ArrayList<WpaBssSta> getBssStations(){
        return  getBssStations(this.get_BSSInterfaceName());
    }

    private ArrayList<WpaBssSta> getBssStations(String intrf ){
        String temp = "";
        ArrayList<WpaBssSta> p_BssSta    = new ArrayList<WpaBssSta>();
        temp = ConsoleTools.RunCmd("./Scripts/getBssStations.sh " + intrf);
        String[] sta = temp.split("\n");
        for (String tmp : sta) {
            String [] params = tmp.split("\t");
            if(params.length == 5){
                try{
                    WpaBssSta newSta = new WpaBssSta();
                    newSta.setSSID(params[4]);
                    newSta.setMAC_ADDR(params[0]);
                    newSta.setFreq(Integer.parseInt(params[1]));
                    newSta.setRSSI(Integer.parseInt(params[2]));
                    if(params[3].contains(WfaEncryption.wpa2))
                        newSta.setSecurity(WfaEncryption.wpa2);
                    else if(params[3].contains(WfaEncryption.wpa))
                        newSta.setSecurity(WfaEncryption.wpa);
                    else if(params[3].contains(WfaEncryption.wep))
                        newSta.setSecurity(WfaEncryption.wep);
                    else 
                        newSta.setSecurity(WfaEncryption.open);
                    
                    if(params[3].contains(WfaChiper.CCMP) && params[3].contains(WfaChiper.TKIP))
                        newSta.setChiper(WfaChiper.CCMP_TKIP);
                    else if(params[3].contains(WfaChiper.CCMP))
                        newSta.setChiper(WfaChiper.CCMP);
                    else if(params[3].contains(WfaChiper.TKIP))
                        newSta.setChiper(WfaChiper.TKIP);
                     
                    p_BssSta.add(newSta);
                }
                catch(Exception ex){}
            }
        }
        return  p_BssSta;
    }

    private void BSS_Scan(){
        BSS_Scan(this.get_BSSInterfaceName());
    }
    
    private void BSS_Scan(String intrf){
        ConsoleTools.RunCmd("./Scripts/bssScan.sh " + intrf);
        m_Scaned = true;
    }
    
    private void UpdateBssStatus(){
        String cmd = "./Scripts/getBssStatus.sh " + this.m_BSSInterfaceName;
        String temp = ConsoleTools.RunCmd(cmd);
        
        String [] params = temp.split("\n");
        //this.m_Self
        for (String tmp : params) {
            String [] pVal = tmp.split("=");
            if(pVal.length == 2){
                String key = pVal[0];
                String val = pVal[1];
                
                if(key.equalsIgnoreCase("SSID")){
                    this.m_Self.setSSID(val);
                }else if (key.equalsIgnoreCase("wpa_state")){
                    this.m_Self.setWpaState(val);
                }
                
            }
        }
    }
    
    private void UpdateBssSignalPoll(){
        String cmd = "./Scripts/getBssSignalPoll.sh " + this.m_BSSInterfaceName;
        String temp = ConsoleTools.RunCmd(cmd);
        
        String [] params = temp.split("\n");
        
        for (String tmp : params) {
            String [] pVal = tmp.split("=");
            if(pVal.length == 2){
                String key = pVal[0];
                String val = pVal[1];
                
                if(key.equalsIgnoreCase("RSSI")){
                    this.m_Self.setRSSI( Integer.parseInt(val));
                }else if (key.equalsIgnoreCase("LINKSPEED")){
                    this.m_Self.setLinkSpeed(Integer.parseInt(val));
                }else if (key.equalsIgnoreCase("FREQUENCY")){
                    this.m_Self.setFreq(Integer.parseInt(val));
                }else if (key.equalsIgnoreCase("AVG_RSSI")){
                    this.m_Self.setAvg_RSSI(Integer.parseInt(val));
                }else if (key.equalsIgnoreCase("WIDTH")){
                    this.m_Self.setWidth(val);
                }
                
            }
        }
    }    
    private ArrayList<networkInterface> getNetworkInterfaces(){
        String temp = "";
        ArrayList<networkInterface> p_Interfeces    = new ArrayList<networkInterface>();

        temp = ConsoleTools.RunCmd("./Scripts/getInterfacesAndMac.sh");
        String[] interfaces = temp.split("\n");

        for (String tmp : interfaces) {
            String[] params = tmp.split(" ");
            if(params.length == 2){
                networkInterface net = new networkInterface();
                net.setNAME(params[0]);
                net.setMAC_ADDR(params[1]);
                net.setIP_ADDR(getIpAddressByInterface(net.getNAME()));
                p_Interfeces.add(net);
            }
        }
        return  p_Interfeces;
    }
    
    
    private boolean isP2pListChanged(ArrayList<WpaP2pSta> p_1,ArrayList<WpaP2pSta> p_2){
        if(p_1.size() != p_2.size())
            return true;
        
        for (WpaP2pSta wpaP2pSta : p_1) {
            if(!p_2.contains(wpaP2pSta))
                return true;
        }
        
        return false;
    }
     
    private boolean isBSSListChanged(ArrayList<WpaBssSta> p_1,ArrayList<WpaBssSta> p_2){
        if(p_1.size() != p_2.size())
            return true;
        
        for (WpaBssSta temp : p_1) {
            if(!p_2.contains(temp))
                return true;
        }
        
        return false;
    }
    
    private boolean isInterfacesChanged(ArrayList<networkInterface> p_1,ArrayList<networkInterface> p_2){
        if(p_1.size() != p_2.size())
            return true;

        for (networkInterface temp : p_1) {
            if(!p_2.contains(temp))
                return true;
        }
        
        return false;
    }
      
    private void GUIUpdateNetworkInterfaces(){
        
        ArrayList<networkInterface> p_Interfeces    = new ArrayList<networkInterface>();
        p_Interfeces = getNetworkInterfaces();
        DefaultTableModel dm = (DefaultTableModel) tblNetworkInterfaces.getModel();
        int rowCount=dm.getRowCount();
        
        if(isInterfacesChanged(p_Interfeces,m_Interfeces) || rowCount < 1)
        {
            m_Interfeces = p_Interfeces;
            for (int i = rowCount-1;i>=0;i--) {
                dm.removeRow(i);
            }
            int i = 0;
            for (networkInterface temp : m_Interfeces) {
                Vector vc = new Vector();
                vc.add(temp.getNAME());
                vc.add(temp.getMAC_ADDR());
                vc.add(temp.getIP_ADDR());
                ((DefaultTableModel) tblNetworkInterfaces.getModel()).insertRow(i, vc); 
                i++;
            }
        }
    }
    
    private void GUIUpdateP2pPeers(){
        ArrayList<WpaP2pSta> p_P2pPeers    = new ArrayList<WpaP2pSta>();
        p_P2pPeers = getP2pPeers();
        DefaultTableModel dm = (DefaultTableModel) tblP2PStations.getModel();
        int rowCount=dm.getRowCount();
        
        try{
            if(isP2pListChanged(p_P2pPeers,m_P2p) || rowCount < 1 || p_P2pPeers.size() ==0 ){
                m_P2p = p_P2pPeers;
                CleanP2pPeersTable();
                int i = 0;
                for (WpaP2pSta temp : m_P2p) {
                    Vector vc = new Vector();
                    vc.add(temp.getMAC_ADDR());
                    vc.add(temp.getNAME());
                    vc.add(temp.getManufactor());
                    vc.add(temp.getListen_freq());
                    dm.insertRow(i, vc); 
                    i++;
                }
            }  
        }catch(Exception ex){
            AppendLog("GUIUpdateP2pPeers error " + ex.getMessage());
        }

    }
    
    private void GUIBssPrintTable(){
        try{
            DefaultTableModel dm = (DefaultTableModel) tblBss.getModel();
            int rowCount=dm.getRowCount();

            for (int i = rowCount-1;i>=0;i--) {
                dm.removeRow(i);
            }
            
            int i = 0;
            String filter = txtBssFilter.getText();
            
            for (WpaBssSta temp : m_Bss) {
                Vector vc = new Vector();
                vc.add(temp.getSSID());
                vc.add(temp.getMAC_ADDR());
                vc.add(temp.getFreq());
                vc.add(temp.getRSSI());
                vc.add(temp.getSecurity());
                vc.add(temp.getChiper());
                vc.add(WfaChannel.FrequencyToChannel(temp.getFreq()));
                if(filter != null && filter.length()>0){
                    if(temp.getSSID().contains(filter) || 
                            temp.getMAC_ADDR().contains(filter) || 
                            Integer.toString(temp.getFreq()).equals(filter) ){
                        dm.insertRow(i, vc); 
                        i++;
                    }
                }
                else{
                    dm.insertRow(i, vc); 
                    i++;
                }
            } 
        }catch(Exception ex){
            AppendLog("[GUIBssPrintTable] error: " + ex.getMessage() );
        }

    }
    private void GUIUpdateBss()
    {
        try{
            ArrayList<WpaBssSta> p_Bss    = new ArrayList<WpaBssSta>();
            p_Bss = getBssStations();
            DefaultTableModel dm = (DefaultTableModel) tblBss.getModel();
            int rowCount=dm.getRowCount();

            if(isBSSListChanged(p_Bss,m_Bss) || rowCount < 1)
            {
                m_Bss = p_Bss;
                GUIBssPrintTable();
            }    
        }catch(Exception ex){
            
        }

        
    }
    

    
    private void CleanP2pPeersTable()
    {
        DefaultTableModel dm = (DefaultTableModel) tblP2PStations.getModel();
        int rowCount=dm.getRowCount();
        for (int i = rowCount-1;i>=0;i--) {
            dm.removeRow(i);
        }
    }
    private ArrayList<WpaP2pSta> getP2pPeers()
    {
        String temp = "";
        ArrayList<WpaP2pSta> p_P2pPeers    = new ArrayList<WpaP2pSta>();
        temp = ConsoleTools.RunCmd("./Scripts/getP2pPeers.sh");
        String[] peers = temp.split("\n");
        for (String tmp : peers) {
            if(tmp.length() >5)
            {    
                WpaP2pSta peer = new WpaP2pSta();
                peer.setMAC_ADDR(tmp);
                String p2p_info = ConsoleTools.RunCmd("./Scripts/getP2pPeerInfo.sh " + peer.MAC_ADDR);
                String[] p2p_info_arr = p2p_info.split("\n");
                for (String tmpParams : p2p_info_arr) {
                    String [] params = tmpParams.split("=");
                    if(params.length == 2)
                    {
                        if(params[0].equalsIgnoreCase("listen_freq")){
                                int lf = -1;
                                if(params[1].length() >0)
                                    lf = Integer.parseInt(params[1]);
                                peer.setListen_freq(lf);
                        }else if(params[0].equalsIgnoreCase("manufacturer")){
                                String man = "Unknown";
                                if(params[1] != null && params[1].length() > 1)
                                    man = params[1];
                                peer.setManufactor(man);
                        }else if(params[0].equalsIgnoreCase( "device_name")){
                                peer.setNAME(params[1]);   
                        }
                    }

                }
                p_P2pPeers.add(peer);
            }
        }
        return  p_P2pPeers;
    }

    /**
     * Creates new form frmMain
     */
    public frmMain() {
        initComponents();
        timer.start();
        timerSlow.start();
    }

    private void DriverReload()
    {
        new Thread(new Runnable() {
            public void run()
            {
                 ConsoleTools.RunCmd("./Scripts/restart.sh");
            }
        }).start();
        txtLog.append("Restarted");
        m_Self.setSSID("");
        m_Self.setRSSI(0);
        m_Scaned = false;
    }
    
    private String getIpAddressByInterface(String intrf)
    {
        return ConsoleTools.RunCmd("./Scripts/getIpAddressByInterface.sh " + intrf).trim();
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblNetworkInterfaces = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        btnBssScan = new javax.swing.JButton();
        btnP2pFind = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        lblP2pPeersCount = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblBssCount = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblRssi = new javax.swing.JLabel();
        lblBssSSID = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblBssWpaState = new javax.swing.JLabel();
        BSSSTATE = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblBssWidth = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblP2PStations = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        txtP2PFilter = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtBssFilter = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblBss = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txtLog.setColumns(20);
        txtLog.setRows(5);
        jScrollPane1.setViewportView(txtLog);

        tblNetworkInterfaces.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "MAC_ADDR", "IP"
            }
        ));
        jScrollPane2.setViewportView(tblNetworkInterfaces);

        jButton1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jButton1.setText("Reload Driver");
        jButton1.setName("btnReloadDriver"); // NOI18N
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnBssScan.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        btnBssScan.setText("Scan");
        btnBssScan.setName("btnReloadDriver"); // NOI18N
        btnBssScan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBssScanMouseClicked(evt);
            }
        });
        btnBssScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBssScanActionPerformed(evt);
            }
        });

        btnP2pFind.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        btnP2pFind.setText("P2P Find");
        btnP2pFind.setName("btnReloadDriver"); // NOI18N
        btnP2pFind.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnP2pFindMouseClicked(evt);
            }
        });
        btnP2pFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnP2pFindActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel2.setText("P2P Peers");

        lblP2pPeersCount.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        lblP2pPeersCount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblP2pPeersCount.setLabelFor(jLabel2);
        lblP2pPeersCount.setText("0");

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel1.setText("BSS count");

        lblBssCount.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        lblBssCount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBssCount.setLabelFor(jLabel1);
        lblBssCount.setText("0");

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel3.setText("RSSI");

        lblRssi.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        lblRssi.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRssi.setLabelFor(jLabel1);
        lblRssi.setText("0");

        lblBssSSID.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        lblBssSSID.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBssSSID.setLabelFor(jLabel1);
        lblBssSSID.setText("0");

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel4.setText("SSID");

        lblBssWpaState.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        lblBssWpaState.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBssWpaState.setLabelFor(jLabel1);
        lblBssWpaState.setText("0");

        BSSSTATE.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        BSSSTATE.setText("State");

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel6.setText("Width");

        lblBssWidth.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        lblBssWidth.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBssWidth.setLabelFor(jLabel1);
        lblBssWidth.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblBssCount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblP2pPeersCount, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnBssScan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnP2pFind, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRssi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblBssSSID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(BSSSTATE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblBssWpaState, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblBssWidth, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBssScan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnP2pFind)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lblP2pPeersCount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblBssCount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BSSSTATE)
                    .addComponent(lblBssWpaState))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblBssSSID))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lblRssi))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblBssWidth))
                .addContainerGap())
        );

        tblP2PStations.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MAC", "NAME", "Manufactor", "Listen Freq"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblP2PStations.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblP2PStationsMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblP2PStations);

        jLabel7.setText("P2P Filter");
        jLabel7.setToolTipText("");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtP2PFilter))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtP2PFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("P2P", jPanel2);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setText("Filter BSS Stations");

        txtBssFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBssFilterActionPerformed(evt);
            }
        });

        tblBss.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        tblBss.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SSID", "MAC_ADDR", "Frequency", "RSSI", "Security", "Chiper", "Channel"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBss.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBssMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblBss);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBssFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 572, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtBssFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1)
                .addGap(1, 1, 1))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jButton1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        DriverReload();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnBssScanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBssScanMouseClicked
        
    }//GEN-LAST:event_btnBssScanMouseClicked

    private void btnBssScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBssScanActionPerformed
        BSS_Scan();
    }//GEN-LAST:event_btnBssScanActionPerformed

    private void btnP2pFindMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnP2pFindMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnP2pFindMouseClicked

    private void btnP2pFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnP2pFindActionPerformed
        CMD_p2p_find(); 
    }//GEN-LAST:event_btnP2pFindActionPerformed

    private void CMD_p2p_find()
    {
        ConsoleTools.RunCmd("sudo wpa_cli -i " + this.get_P2PInterfaceName() +" p2p_find");
    }
    
    private void StartP2pConnection(String PeerMac)
    {
        StartP2pConnection frm = new StartP2pConnection(getP2pStaByMac(PeerMac));
        frm.show();
    }
    
    private WpaP2pSta getP2pStaByMac(String mac)
    {
        WpaP2pSta ret = null;
        
        for (WpaP2pSta temp : m_P2p) {
            if(temp.getMAC_ADDR().equals(mac)){
                ret = temp;
                break;
            }
        }
        return  ret;
    }
    
    private WpaBssSta getBssStaByMac(String mac)
    {
        WpaBssSta ret = null;
        
        for (WpaBssSta temp : m_Bss) {
            if(temp.getMAC_ADDR().equals(mac)){
                ret = temp;
                break;
            }
        }
        return  ret;
    }
    
    private void tblP2PStationsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblP2PStationsMouseClicked
        if(evt.getClickCount() == 2)
        {
            DefaultTableModel dm = (DefaultTableModel) tblP2PStations.getModel();
            int selected = tblP2PStations.getSelectedRow();
            System.out.println("Selected:" + selected);
            
            final JTable target = (JTable)evt.getSource();
            final int row = target.getSelectedRow();
            final int column = target.getSelectedColumn();
            //Cast to ur Object type
            Object obj = target.getValueAt(row, 0);
            if(obj != null)
            {
                String peer_mac = obj.toString();
                this.StartP2pConnection(peer_mac);
                AppendLog(peer_mac);
            }
        }
    }//GEN-LAST:event_tblP2PStationsMouseClicked

    private void tblBssMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBssMouseClicked

        if(evt.getClickCount() == 2)
        {
            DefaultTableModel dm = (DefaultTableModel) tblBss.getModel();
            int selected = tblBss.getSelectedRow();
            System.out.println("Selected:" + selected);
            
            final JTable target = (JTable)evt.getSource();
            final int row = target.getSelectedRow();
            final int column = target.getSelectedColumn();
            //Cast to ur Object type
            Object obj = target.getValueAt(row, 1);
            if(obj != null){
                String peer_mac = obj.toString();
                this.StartBssConnection(peer_mac);
                AppendLog(peer_mac);
            }
        }

    }//GEN-LAST:event_tblBssMouseClicked

    private void txtBssFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBssFilterActionPerformed
        GUIBssPrintTable();
    }//GEN-LAST:event_txtBssFilterActionPerformed
    
    private void StartBssConnection(String PeerMac){
        frmBssConnect frm = new frmBssConnect(getBssStaByMac(PeerMac));
        frm.show();
    }
    
    private void AppendLog(String txt)
    {
        txtLog.append("\n"+ txt);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BSSSTATE;
    private javax.swing.JButton btnBssScan;
    private javax.swing.JButton btnP2pFind;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblBssCount;
    private javax.swing.JLabel lblBssSSID;
    private javax.swing.JLabel lblBssWidth;
    private javax.swing.JLabel lblBssWpaState;
    private javax.swing.JLabel lblP2pPeersCount;
    private javax.swing.JLabel lblRssi;
    private javax.swing.JTable tblBss;
    private javax.swing.JTable tblNetworkInterfaces;
    private javax.swing.JTable tblP2PStations;
    private javax.swing.JTextField txtBssFilter;
    private javax.swing.JTextArea txtLog;
    private javax.swing.JTextField txtP2PFilter;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the m_BSSInterfaceName
     */
    public String get_BSSInterfaceName() {
        return m_BSSInterfaceName;
    }

    /**
     * @param m_BSSInterfaceName the m_BSSInterfaceName to set
     */
    public void set_BSSInterfaceName(String m_BSSInterfaceName) {
        this.m_BSSInterfaceName = m_BSSInterfaceName;
    }

    /**
     * @return the m_P2PInterfaceName
     */
    public String get_P2PInterfaceName() {
        return m_P2PInterfaceName;
    }

    /**
     * @param m_P2PInterfaceName the m_P2PInterfaceName to set
     */
    public void set_P2PInterfaceName(String m_P2PInterfaceName) {
        this.m_P2PInterfaceName = m_P2PInterfaceName;
    }
}
