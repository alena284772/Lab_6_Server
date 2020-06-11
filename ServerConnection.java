package ServerSide;


import Commands.Command;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.logging.Logger;

public class ServerConnection {
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Boolean isworking = true;
    private CollectionManager collectionManager=new CollectionManager();
    private static Logger logger=Logger.getLogger(ServerConnection.class.getName());



            public void work() {
                System.out.println("Enter port(The client will come to port 7415):");
                Scanner scanner=new Scanner(System.in);
                int port = -1;
                while (port == -1){
                    try{
                        int a = Integer.valueOf(scanner.nextLine().trim());
                        if (a<0 || a > 65535){
                            logger.info("Wrong port was entered. Port should be a number from 0 to 65535");
                        }else{
                            port = a;
                            logger.info("Port is now: "+port);
                        }
                    }catch (NumberFormatException e){
                        logger.info("Entered value is not a number");
                    }

                }
                try {
                    ServerSocket ss = new ServerSocket(port);
                    logger.info("ServerSocket awaiting connections...");
                    try {
                        Socket socket = ss.accept();
                        logger.info("Connection from " + socket + "!");
                        OutputStream out = socket.getOutputStream();
                        logger.info("Server validates collection file");
                        Command command17 = new Command();
                        collectionManager.read_file("11.xml", command17);
                        logger.info(command17.getAnswer());

                while ( isworking) {

                    //this.send(socket, command17, out);

                    Command comin = new Command();
                    while (comin.getName() == null&& isworking) {
                        try {
                            this.read(socket, comin);

                        } catch (EOFException e) {
                            logger.info("Cannot reading");
                            isworking=false;
                        }
                    }

                    if (comin.getName() != null) {

                        switch (comin.getName()) {

                            case ("exit"):
                                logger.info("The server executes the command: exit ");
                                Command command16 = new Command();
                                collectionManager.save(command16);
                                //this.send(socket, command16, out);
                                isworking = false;
                                break;

                            case ("info"):
                                logger.info("The server executes the command: info");
                                Command command0 = new Command();
                                command0.setAnswer(collectionManager.toString());
                                this.send(socket, command0, out);
                                break;
                            case ("clear"):
                                logger.info("The server executes the command: clear");
                                Command command8 = new Command();
                                collectionManager.clear(command8);
                                this.send(socket, command8, out);
                                break;

                            case ("remove_greater"):
                                logger.info("The server executes the command: remove_greater");
                                Command command1 = new Command();
                                collectionManager.remove_greater(comin.getVehicle(), command1);
                                this.send(socket, command1, out);
                                break;
                            case ("insert"):
                                logger.info("The server executes the command: insert");
                                Command command2 = new Command();
                                collectionManager.insert(comin.getKey(), comin.getVehicle(), command2);
                                this.send(socket, command2, out);
                                break;
                            case ("show"):
                                logger.info("The server executes the command: show");
                                Command command10 = new Command();
                                collectionManager.show(command10);
                                this.send(socket, command10, out);
                                break;
                            case ("remove_key"):
                                logger.info("The server executes the command: remove_key");
                                Command command3 = new Command();
                                collectionManager.remove_key(comin.getKey(), command3);
                                this.send(socket, command3, out);
                                break;
                            case ("replace_if_greater"):
                                logger.info("The server executes the command: replace_if_greater");
                                Command command4 = new Command();
                                collectionManager.replace_if_greater(comin.getKey(), comin.getVehicle(), command4);
                                this.send(socket, command4, out);
                                break;
                            case ("remove_any_by_number_of_wheels"):
                                logger.info("The server executes the command: remove_any_by_number_of_wheels");
                                Command command5 = new Command();
                                collectionManager.remove_any_by_number_of_wheels(comin.getNumber(), command5);
                                this.send(socket, command5, out);
                                break;
                            case ("count_less_than_engine_power"):
                                logger.info("The server executes the command: count_less_than_engine_power");
                                Command command6 = new Command();
                                collectionManager.count_less_than_engine_power(comin.getPower(), command6);
                                this.send(socket, command6, out);
                                break;
                            case ("update"):
                                logger.info("The server executes the command: update");
                                Command command7 = new Command();
                                collectionManager.update(comin.getID(), comin.getVehicle(), command7);
                                this.send(socket, command7, out);
                                break;

                            default:
                                logger.info("This is an unknown command.");
                                break;

                        }
                    }
                    else {
                        socket.close();
                    }
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }  catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
           System.out.println("Address already in use");
          work();
        }
    }


    public void read(Socket socket, Command command) throws  IOException, ClassNotFoundException {

            try{

            logger.info("Server reading command from client");
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[163840];
            inputStream.read(bytes);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            Command com = (Command) object;
            command.setID(com.getID());
            command.setName(com.getName());
            command.setKey(com.getKey());
            command.setVehicle(com.getVehicle());
            command.setNumber(com.getNumber());
            command.setPower(com.getPower());
            command.setFile_name(com.getFile_name());
            byteArrayInputStream.close();
            objectInputStream.close();
            }catch (SocketException|StreamCorruptedException e){
                //e.printStackTrace();
                isworking=false;
            }


    }

    public void send(Socket socket,Command command,OutputStream out) throws IOException {
        try {
            byte[] bytes = new byte[163840];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(command);
            objectOutputStream.flush();
            bytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.flush();
            out.write(bytes);
            objectOutputStream.close();
            byteArrayOutputStream.close();
            logger.info("Server send a message");
        } catch (IOException e){
            logger.info("MISTAKE ! Server cannot send a message");
            e.printStackTrace();
        }
    }
    public Integer port_check(String string, Scanner scanner) {
        Integer port = null;
        try {
            port = Integer.valueOf(string);
        } catch (NumberFormatException E) {
            System.out.println("Input arg of port is incorrect.It must be Integer value. Try again");
            while (port == null) {
                System.out.println("Port:");
                try {
                    port = Integer.valueOf(scanner.next());
                } catch (NumberFormatException e) {
                    System.out.println("Input arg of port is incorrect. Try again");
                }
            }
        }
        return port;
    }

}



