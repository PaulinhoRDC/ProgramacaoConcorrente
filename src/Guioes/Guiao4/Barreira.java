import java.util.concurrent.Semaphore;

class Main {
    public static void main(String[] args){
        Barreira b = new Barreira(3);
        new Thread(() -> {
            try {
                Thread.sleep(100);
                System.out.println("Vou fazer await - 1");
                b.await();
                System.out.println("wait retornou - 1");
            }
            catch(Exception e ){}
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(10000);
                System.out.println("Vou fazer await - 2");
                b.await();
                System.out.println("wait retornou - 2");
            }
            catch(Exception e ){}
        }).start();


        new Thread(() -> {
            try {
                Thread.sleep(2000);
                System.out.println("Vou fazer await - 3");
                b.await();
                System.out.println("wait retornou - 3");
            }
            catch(Exception e ){}
        }).start();
    }
}

public class Barreira {

    private final int N;
    private int c = 0;

    private Semaphore mut = new Semaphore(1);
    private Semaphore sem = new Semaphore(0);

    public Barreira(int N) {
        this.N = N;
    }

    public void await() throws InterruptedException {
        mut.acquire();
        c += 1;
        int v = c;
        mut.release();

        if (v < N) {                        // não sou o último
            sem.acquire();
        } else {                              // sou o último
            for (int i = 0; i < N-1; i++) {
                sem.release();
            }
        }

        /*
        if (v == N){
            for (int i = 0; i<N ; i++){
                sem.release();
            }
            sem.acquire();
        }

         */
    }
}

