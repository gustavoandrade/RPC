package nameserver;

import connection.ReceiverConnection;
import connection.SenderConnection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NameServer extends SenderConnection{
    HashMap<String ,String> routingTable;
    int i = 0;

    public NameServer() {
        this.routingTable = new HashMap<>();
        new Thread(new SkeletonListener()).start();
        new Thread(new StubListener()).start();
    }

    private class SkeletonListener extends ReceiverConnection implements Runnable{
        public SkeletonListener() {
            try {
                this.doReceiverConnection(7796);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run(){
            while (true){
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String received= new String(packet.getData(), 0, packet.getLength());
                    addService(received);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private class StubListener extends ReceiverConnection implements Runnable{
        public StubListener() {
            try {
                this.doReceiverConnection(7795);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run(){
            while (true){
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String received= new String(packet.getData(), 0, packet.getLength());
                    sendIP(getHostname(received));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addService(String hostLocation){
        routingTable.put(hostLocation, "Robot"+i);
        System.out.print("[ADDED] " + hostLocation + " as " + routingTable.get(hostLocation) );
        try {
            this.doSenderConnection();
            this.sendMessage(routingTable.get(hostLocation),7792);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void removeService(String hostLocation){
        routingTable.remove(hostLocation);
        i--;
    }
    private String getHostname(String name){
        for (Map.Entry<String, String> entry : routingTable.entrySet()) {
            if (Objects.equals(name, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
    private void sendIP(String ip){
        try {
            this.doSenderConnection();
            this.sendMessage(ip,7792);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        NameServer dns = new NameServer();
    }
}
