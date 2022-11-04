import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(9292)){
            Socket clientSocket = serverSocket.accept();
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            long start = System.currentTimeMillis();
            sendFile("Location of your file with extension");
            long end = System.currentTimeMillis();
            System.out.println("The file was transferred or received for: " + (end - start) + " millisecond");

            dataInputStream.close();
            dataOutputStream.close();
            clientSocket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void sendFile(String path) throws Exception{
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        dataOutputStream.writeLong(file.length());
        byte[] buffer = new byte[64*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
    }
    private static void receiveFile(String fileName) throws Exception{
        int bytes = 0;
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);

        long size = dataInputStream.readLong();
        byte[] buffer = new byte[64*1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes;
        }
        fileOutputStream.close();
    }
}