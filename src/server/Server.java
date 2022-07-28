package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static void main(String[] args) throws IOException {
        ArrayList<User> users = new ArrayList<>();
        try {
            ServerSocket serverSocket = new ServerSocket(9445);
            System.out.println("Сервер запущен");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключился");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String name = "";
                        try {
                            User user = new User(socket);
                            users.add(user);
                            DataInputStream is = user.getIs();
                            DataOutputStream out = user.getOut();
                            out.writeUTF("Введите имя");
                            name = is.readUTF();

                            while (true) {
                                System.out.println();
                                String message = is.readUTF();
                                if (!message.isEmpty()) {
                                    for (User us : users) {
                                        if (!(us.getUuid().equals(user.getUuid()))) {
                                            DataOutputStream out1 = us.getOut(); //new DataOutputStream(us.getSocket().getOutputStream());
                                            out1.writeUTF(name + ": " + message);
                                        }
                                    }
                                }
                            }
                        } catch (IOException e) {
                            for (User us : users) {
                                try {
                                    DataOutputStream out1 = us.getOut();
                                    out1.writeUTF(name + ": отключился ");
                                    System.out.println(name + ": отключился");
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                });
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
