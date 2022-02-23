package Guioes.Guiao1;

class MyThread extends Thread {
    public void run() {                            // TOPO DA MAIN
        try {
            System.out.println("Hello World");
            Thread.sleep(500);
            sleep(500);
            System.out.println("Hello World");
        } catch (InterruptedException ignored) {}
    }
}


class MyRunnable implements Runnable {
    public void run(){
        System.out.println("Runnable");
    }
}



class Main4 {
    public static void main (String [] args) throws InterruptedException {


        Thread t1 = new MyThread();
        Thread t2 = new MyThread();
        t1.start();                 // intuito de dar start na Thread
        t2.start();
        t1.join();                  // intuito de esperar que a Thread termine
        t2.join();

        System.out.println("Main");

        Thread t = new Thread(new MyRunnable());                // OUTRA ESTRATÉGIA
        t.start();

    }
}
