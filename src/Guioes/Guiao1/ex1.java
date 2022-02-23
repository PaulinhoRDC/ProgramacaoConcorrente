package Guioes.Guiao1;

class Printer extends Thread {
    final int iterations;

    Printer(int iterations) {
        this.iterations = iterations;
    }

    public void run(){
        for(int i=0;i<iterations;i++){
            System.out.println(i+1);
        }
    }
}

class Main {
    public static void main (String [] args) throws InterruptedException {

        final int N = Integer.parseInt(args[0]);
        final int I = Integer.parseInt(args[1]);

        for (int i = 0; i<N; i++){
            new Printer(I).start();
        }

    }
}