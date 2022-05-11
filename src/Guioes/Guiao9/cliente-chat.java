import java.io.*;
import java.net.*;

class Client {
    public static void main(String[] args){
        try{
            if(args.length<2)
                System.exit(1);
            
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            Socket s = new Socket(host, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream());

            new Thread(() -> {
                try {
                    while(true){
                        String res = System.console().readLine();
                        out.println(res);
                        out.flush();
                    }

                } catch (Exception e) {}  
            }).start();

            new Thread(() -> {
                try {
                    while(true){
                        String res = in.readLine();
                        System.out.println(res);                       
                    }

                } catch (Exception e) {}  
            }).start();
        }catch (Exception e){}
    }
}