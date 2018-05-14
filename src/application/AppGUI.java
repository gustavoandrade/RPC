package application;

import connection.ReceiverConnection;
import connection.SenderConnection;
import message.Message;
import org.cads.ev3.gui.ICaDSRobotGUIUpdater;
import org.cads.ev3.gui.swing.CaDSRobotGUISwing;
import org.cads.ev3.rmi.consumer.ICaDSRMIConsumer;
import org.cads.ev3.rmi.generated.cadSRMIInterface.IIDLCaDSEV3RMIMoveGripper;
import org.cads.ev3.rmi.generated.cadSRMIInterface.IIDLCaDSEV3RMIMoveHorizontal;
import org.cads.ev3.rmi.generated.cadSRMIInterface.IIDLCaDSEV3RMIMoveVertical;
import org.cads.ev3.rmi.generated.cadSRMIInterface.IIDLCaDSEV3RMIUltraSonic;

import java.io.*;
import java.net.*;

public class AppGUI extends SenderConnection implements IIDLCaDSEV3RMIMoveGripper, IIDLCaDSEV3RMIMoveHorizontal, IIDLCaDSEV3RMIMoveVertical, IIDLCaDSEV3RMIUltraSonic, ICaDSRMIConsumer {
     CaDSRobotGUISwing gui;

     public AppGUI() {
        CaDSRobotGUISwing gui = new CaDSRobotGUISwing(this,this,this,this,this);
        new Thread(new StubListener()).start();
    }

    private class StubListener extends ReceiverConnection implements  Runnable {
         DatagramSocket socketListener;
         protected byte[] buf = new byte[256];

         public StubListener() {
             try {
                 socketListener = new DatagramSocket(7793);
             } catch (SocketException e) {
                 e.printStackTrace();
             }
         }

         @Override
         public void run() {
             while (true) {
                 try {
                     DatagramPacket packet = new DatagramPacket(buf, buf.length);
                     socketListener.receive(packet);
                     InetAddress address = packet.getAddress();
                     int port = packet.getPort();
                     packet = new DatagramPacket(buf, buf.length, address, port);
                     String received = new String(packet.getData(), 0, packet.getLength());
                     gui.addService(received);
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }

     }
     
    public void sendMessage(Message m){
        try {
            this.doSenderConnection();
            this.sendMessage(m,7798);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(ICaDSRobotGUIUpdater observer) {
        System.out.println("New Observer");
        observer.addService("Service 1");
        observer.addService("Service 2");
        observer.setChoosenService("Service 2", -1, -1, false);
    }

    @Override
    public void update(String s) {

    }

    @Override
    public int closeGripper(int transactionID) throws Exception {
        System.out.println("Close.... TID: " + transactionID);
        return 0;
    }

    @Override
    public int openGripper(int transactionID) throws Exception {
        System.out.println("open.... TID: " + transactionID);
        return 0;
    }

    @Override
    public int isGripperClosed() throws Exception {
        return 0;
    }

    @Override
    public int moveHorizontalToPercent(int transactionID, int percent) throws Exception {
        System.out.println("Call to move vertical -  TID: " + transactionID + " degree " + percent);
        sendMessage(new Message("horizontal",transactionID,percent));
        return 0;
    }

    @Override
    public int moveVerticalToPercent(int transactionID, int percent) throws Exception {
        System.out.println("Call to move vertical -  TID: " + transactionID + " degree " + percent);
        return 0;
    }

    @Override
    public int stop(int transactionID) throws Exception {
        System.out.println("Stop movement.... TID: " + transactionID);
        return 0;
    }

    @Override
    public int getCurrentVerticalPercent() throws Exception {
        return 0;
    }

    @Override
    public int getCurrentHorizontalPercent() throws Exception {
        return 0;
    }

    @Override
    public int isUltraSonicOccupied() throws Exception {
        return 0;
    }

    public static void main(String[] args){
        AppGUI g = new AppGUI();
    }
}
