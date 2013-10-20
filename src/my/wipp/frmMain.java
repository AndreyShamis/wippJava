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
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    
    /**
     * 
     * @return 
     */
    private ArrayList<WpaBssSta> getBssStations()
    {
        return  getBssStations(this.get_BSSInterfaceName());
    }

    private ArrayList<WpaBssSta> getBssStations(String intrf )
    {
        String temp = "";
        ArrayList<WpaBssSta> p_BssSta    = new ArrayList<WpaBssSta>();
        temp = RunCmd("./Scripts/getBssStations.sh " + intrf);
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
    
    private void BSS_Scan(String intrf)
    {
        RunCmd("./Scripts/bssScan.sh " + intrf);
        m_Scaned = true;
    }
    
    private ArrayList<networkInterface> getNetworkInterfaces()
    {
        String temp = "";
        ArrayList<networkInterface> p_Interfeces    = new ArrayList<networkInterface>();

        temp = RunCmd("./Scripts/getInterfacesAndMac.sh");
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
    
    
    private boolean isP2pListChanged(ArrayList<WpaP2pSta> p_1,ArrayList<WpaP2pSta> p_2)
    {
        if(p_1.size() != p_2.size())
            return true;
        
        for (WpaP2pSta wpaP2pSta : p_1) {
            if(!p_2.contains(wpaP2pSta))
                return true;
        }
        
        return false;
    }
     
    private boolean isBSSListChanged(ArrayList<WpaBssSta> p_1,ArrayList<WpaBssSta> p_2)
    {
        if(p_1.size() != p_2.size())
            return true;
        
        for (WpaBssSta temp : p_1) {
            if(!p_2.contains(temp))
                return true;
        }
        
        return false;
    }
    
    private boolean isInterfacesChanged(ArrayList<networkInterface> p_1,ArrayList<networkInterface> p_2)
    {
        if(p_1.size() != p_2.size())
            return true;

        for (networkInterface temp : p_1) {
            if(!p_2.contains(temp))
                return true;
        }
        
        return false;
    }
      
    private void GUIUpdateNetworkInterfaces()
    {
        
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
    
    private void GUIUpdateP2pPeers()
    {
        ArrayList<WpaP2pSta> p_P2pPeers    = new ArrayList<WpaP2pSta>();
        p_P2pPeers = getP2pPeers();
        DefaultTableModel dm = (DefaultTableModel) tblP2PStations.getModel();
        int rowCount=dm.getRowCount();
        
        try{
            if(isP2pListChanged(p_P2pPeers,m_P2p) || rowCount < 1){
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
    
    private void GUIUpdateBss()
    {
        ArrayList<WpaBssSta> p_Bss    = new ArrayList<WpaBssSta>();
        p_Bss = getBssStations();
        DefaultTableModel dm = (DefaultTableModel) tblBss.getModel();
        int rowCount=dm.getRowCount();
        
        if(isBSSListChanged(p_Bss,m_Bss) || rowCount < 1)
        {
            m_Bss = p_Bss;
            for (int i = rowCount-1;i>=0;i--) {
                dm.removeRow(i);
            }
            int i = 0;
            for (WpaBssSta temp : m_Bss) {
                Vector vc = new Vector();
                vc.add(temp.getSSID());
                vc.add(temp.getMAC_ADDR());
                vc.add(temp.Freq);
                vc.add(temp.RSSI);
                dm.insertRow(i, vc); 
                i++;
            }
        }
    }
    
    ActionListener al = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            try{
                GUIUpdateNetworkInterfaces();
                GUIUpdateP2pPeers();
                GUIUpdateBss();
                lblBssCount.setText(Integer.toString(m_Bss.size()));
                lblP2pPeersCount.setText(Integer.toString(m_P2p.size()));
            }catch(Exception ex){
                //AppendLog("Timer error:" + ex.getMessage());
            }
        }
    };
    private final Timer timer  = new Timer(700,al);
    
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
        temp = RunCmd("./Scripts/getP2pPeers.sh");
        String[] peers = temp.split("\n");
        for (String tmp : peers) {
            if(tmp.length() >5)
            {    
                WpaP2pSta peer = new WpaP2pSta();
                peer.setMAC_ADDR(tmp);
                String p2p_info = RunCmd("./Scripts/getP2pPeerInfo.sh " + peer.MAC_ADDR);
                String[] p2p_info_arr = p2p_info.split("\n");
                for (String tmpParams : p2p_info_arr) {
                    String [] params = tmpParams.split("=");
                    if(params.length == 2)
                    {
                        if(params[0].equals("listen_freq")){
                                int lf = -1;
                                if(params[1].length() >0)
                                    lf = Integer.parseInt(params[1]);
                                peer.setListen_freq(lf);
                        }else if(params[0].equals("manufacturer")){
                                String man = "Unknown";
                                if(params[1] != null && params[1].length() > 1)
                                    man = params[1];
                                peer.setManufactor(man);
                        }else if(params[0].equals( "device_name")){
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
    }

    private void DriverReload()
    {
        new Thread(new Runnable() {
            public void run()
            {
                 RunCmd("./Scripts/restart.sh");
            }
        }).start();
        m_Scaned = false;
    }
    
    private String getIpAddressByInterface(String intrf)
    {
        return RunCmd("./Scripts/getIpAddressByInterface.sh " + intrf).trim();
    }
    
    public String RunCmd(String cmd) 
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
                AppendLog("[" + cmd + "]Exit code = " + proc.exitValue() + " " +ret);
                String errStr = err.readLine();
                if(errStr != null)
                    AppendLog(errStr);
            }
        }
        catch(IOException e)
        {
            AppendLog(e.getMessage());
        } catch (InterruptedException ex) {
            AppendLog(ex.getMessage());
            Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ret;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        tblP2PStations = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblNetworkInterfaces = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblBss = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        btnBssScan = new javax.swing.JButton();
        btnP2pFind = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        lblP2pPeersCount = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblBssCount = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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

        tblBss.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SSID", "MAC_ADDR", "Frequency", "RSSI"
            }
        ));
        jScrollPane4.setViewportView(tblBss);

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblBssCount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblP2pPeersCount, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnBssScan, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnP2pFind, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(lblBssCount)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane4)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 554, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE))
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
        RunCmd("sudo wpa_cli -i " + this.get_P2PInterfaceName() +" p2p_find");
    }
    
    private void tblP2PStationsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblP2PStationsMouseClicked
        if(evt.getClickCount() == 2)
        {
            DefaultTableModel dm = (DefaultTableModel) tblP2PStations.getModel();
            int selected = tblP2PStations.getSelectedRow();
            System.out.println("Selected:" + selected);
        }
    }//GEN-LAST:event_tblP2PStationsMouseClicked

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
    private javax.swing.JButton btnBssScan;
    private javax.swing.JButton btnP2pFind;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblBssCount;
    private javax.swing.JLabel lblP2pPeersCount;
    private javax.swing.JTable tblBss;
    private javax.swing.JTable tblNetworkInterfaces;
    private javax.swing.JTable tblP2PStations;
    private javax.swing.JTextArea txtLog;
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
