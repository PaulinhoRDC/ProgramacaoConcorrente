import java.util.concurrent.Semaphore;

class Main {
    public static void main(String[] args) throws InterruptedException {
        BoundedBuffer b = new BoundedBuffer(20);

        new Thread(() -> {
            try {
                for (int i=1;; i++) {
                    System.out.println("Put de " + i);
                    b.put(i);
                    System.out.println("Put done \n");
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                for (int i=1;; i++) {
                    System.out.println("Get de: " + i);
                    int c = b.get();
                    System.out.println("get retornou " + c);
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }
}


public class BoundedBuffer<T> {                       // depois, pensar que o Int, poderia ser um T (tipo genérico)

    private int[] buf;
    private int iget = 0;                   // índice onde faço get's
    private int iput = 0;                   // índice onde faço put's

    //private int len = 0;                                PARA IMPLEMENTAÇÃO SEQUENCIAL

    Semaphore items;
    Semaphore slots;
    Semaphore mutget = new Semaphore(1);
    Semaphore mutput = new Semaphore(1);


    public BoundedBuffer(int N) {
        buf = new int[N];
        items = new Semaphore(0);
        slots = new Semaphore(N);

    }

    public int get() throws InterruptedException {
        int res;

        //if (len == 0) throw ...                          PARA IMPLEMENTAÇÃO SEQUENCIAL
        items.acquire();
        mutget.acquire();

        res = buf[iget];
        iget = (iget + 1) % buf.length;
        //len -= 1;

        mutget.release();
        slots.release();
        return res;
    }

    public void put(int v) throws InterruptedException {

        slots.acquire();
        mutput.acquire();

        //if (len == buf.length) throw ...                          PARA IMPLEMENTAÇÃO SEQUENCIAL
        buf[iput] = v;
        iput = (iput + 1) % buf.length;
        //len += 1;

        mutput.release();
        items.release();

    }
}

/*
class Producer extends Thread{
    public void run(){}                // passou para um runnable dentro da main
}
*/



