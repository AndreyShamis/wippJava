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

    private ArrayList<WpaBssSta>        m_Bss           = new ArrayList<>();
    private ArrayList<WpaP2pSta>        m_P2p           = new ArrayList<>();
    private ArrayList<networkInterface> m_Interfeces    = new ArrayList<>();
    
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
        return  getBssStations("wlan0");
    }
    
    private ArrayList<WpaBssSta> getBssStations(String intrf )
    {
//        if(m_Scaned == false)
//        {
//            BSS_Scan("");
//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        String temp = "";
        ArrayList<WpaBssSta> p_BssSta    = new ArrayList<>();
        try {
            temp = RunCmd("./Scripts/getBssStations.sh " + intrf);
            String[] sta = temp.split("\n");
            for (String tmp : sta) {
                String [] params = tmp.split(" ");
                
                if(params.length == 4)
                {
                    WpaBssSta newSta = new WpaBssSta();
                    newSta.setSSID(params[0]);
                    p_BssSta.add(newSta);
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return  p_BssSta;
    }

    private void BSS_Scan(){
        BSS_Scan("wlan0");
    }
    
    private void BSS_Scan(String intrf)
    {
        try {
            RunCmd("./Scripts/bssScan.sh " + intrf);
            m_Scaned = true;
        } catch (IOException ex) {
            Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private ArrayList<networkInterface> UpdateNetworkInterfaces()
    {
        String temp = "";
        ArrayList<networkInterface> p_Interfeces    = new ArrayList<>();
        try {
            // TODO add your handling code here:
            temp = RunCmd("./Scripts/getInterfacesAndMac.sh");
            String[] interfaces = temp.split("\n");
            
            for (String tmp : interfaces) {
                String[] params = tmp.split(" ");
                if(params.length == 2){
                    networkInterface net = new networkInterface();
                    net.setNAME(params[0]);
                    net.setMAC_ADDR(params[1]);
                    net.setIP_ADDR(RunCmd("/home/werd/devel/wippJava/Scripts/getIpAddressByInterface.sh " + net.getNAME()).trim());
                    p_Interfeces.add(net);
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return  p_Interfeces;
    }
    
    private ArrayList<WpaP2pSta> UpdateP2pPeersTable()
    {
        String temp = "";
        ArrayList<WpaP2pSta> p_P2pPeers    = new ArrayList<>();
        try {
            temp = RunCmd("./Scripts/getP2pPeers.sh");
            String[] peers = temp.split("\n");
            for (String tmp : peers) {
                WpaP2pSta peer = new WpaP2pSta();
                peer.setMAC_ADDR(tmp);
                String p2p_info = RunCmd("/home/werd/devel/wippJava/Scripts/getP2pPeerInfo.sh " + peer.MAC_ADDR);
                String[] p2p_info_arr = p2p_info.split("\n");
                for (String tmpParams : p2p_info_arr) {
                    String [] params = tmpParams.split("=");
                    if(params.length == 2)
                    {
                        switch (params[0]){
                            case "listen_freq":
                                peer.setListen_freq( Integer.parseInt(params[1]));
                                break;
                            case "manufacturer":
                                peer.setManufactor(params[1]);
                                break;
                            case "device_name":
                                peer.setNAME(params[1]);
                                break;
                        }
                    }

                }
                p_P2pPeers.add(peer);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return  p_P2pPeers;
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
        
        ArrayList<networkInterface> p_Interfeces    = new ArrayList<>();
        p_Interfeces = UpdateNetworkInterfaces();
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
        ArrayList<WpaP2pSta> p_P2pPeers    = new ArrayList<>();
        p_P2pPeers = UpdateP2pPeersTable();
        DefaultTableModel dm = (DefaultTableModel) tblP2PStations.getModel();
        int rowCount=dm.getRowCount();
        
        if(isP2pListChanged(p_P2pPeers,m_P2p) || rowCount < 1)
        {
            m_P2p = p_P2pPeers;
            for (int i = rowCount-1;i>=0;i--) {
                dm.removeRow(i);
            }
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
    }
    
    private void GUIUpdateBss()
    {
        ArrayList<WpaBssSta> p_Bss    = new ArrayList<>();
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
            UpdateP2pPeersTable();

            GUIUpdateNetworkInterfaces();
            GUIUpdateP2pPeers();
        }
    };
    private final Timer timer  = new Timer(300,al);
    
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
        ArrayList<WpaP2pSta> p_P2p = new ArrayList<>();

        return p_P2p;
    }

    /**
     * Creates new form frmMain
     */
    public frmMain() {
        initComponents();
        WpaBssSta sta = new WpaBssSta();
        sta.Freq = 12;
        sta.MAC_ADDR = "00:00:00:00:00:00";
        sta.RSSI = -45;
        sta.SSID = "HelloWorld";
        

        timer.start();
    }

    private void DriverReload()
    {
        try {
            // TODO add your handling code here:
            RunCmd("./Scripts/restart.sh");
            m_Scaned = false;
        } catch (IOException ex) {
            Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String getIpAddressByInterface(String intrf)
    {
        String ret = "";
        try {
            // TODO add your handling code here:
            ret = RunCmd("./Scripts/getIpAddressByInterface.sh " + intrf).trim();
        } catch (IOException ex) {
            Logger.getLogger(frmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    public String RunCmd(String cmd) throws IOException
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
        }
        catch(IOException e)
        {
            AppendLog(e.getMessage());
        } catch (InterruptedException ex) {
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

        jButton1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblP2PStations = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        btnBssScan = new javax.swing.JButton();
        btnP2pFind = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblNetworkInterfaces = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblBss = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setFont(new java.awt.Font("Dialog", 1, 8)); // NOI18N
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

        btnBssScan.setFont(new java.awt.Font("Dialog", 1, 8)); // NOI18N
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

        btnP2pFind.setFont(new java.awt.Font("Dialog", 1, 8)); // NOI18N
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBssScan, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnP2pFind, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(btnBssScan)
                            .addComponent(btnP2pFind))))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(27, Short.MAX_VALUE))
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
        BSS_Scan();
    }//GEN-LAST:event_btnBssScanMouseClicked

    private void btnBssScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBssScanActionPerformed
        // TODO add your handling code here:
        m_P2p = getP2pPeers();
        AppendLog(getIpAddressByInterface("eth0"));
    }//GEN-LAST:event_btnBssScanActionPerformed

    private void btnP2pFindMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnP2pFindMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnP2pFindMouseClicked

    private void btnP2pFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnP2pFindActionPerformed
        WpaP2pSta p2psta = new WpaP2pSta();
        p2psta.MAC_ADDR = "01:01:01:04:05:06";
        p2psta.Manufactor = "Intel";
        p2psta.NAME = "BAL BLA BLA";
        p2psta.listen_freq = 2412;
        
        WpaP2pSta p2psta2 = new WpaP2pSta();
        p2psta2.MAC_ADDR = "02:01:01:04:05:06";
        p2psta2.Manufactor = "In22tel";
        p2psta2.NAME = "B22AL BLA BLA";
        p2psta2.listen_freq = 2112;
        
        m_P2p.add(p2psta);
        m_P2p.add(p2psta2);
    }//GEN-LAST:event_btnP2pFindActionPerformed

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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable tblBss;
    private javax.swing.JTable tblNetworkInterfaces;
    private javax.swing.JTable tblP2PStations;
    private javax.swing.JTextArea txtLog;
    // End of variables declaration//GEN-END:variables
}
