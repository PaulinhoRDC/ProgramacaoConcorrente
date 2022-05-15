package Guioes.Guiao5;

class Main {
    public static void main(String[] args) throws InterruptedException {
        BoundedBufferMonitors b = new BoundedBufferMonitors(20);

        new Thread(() -> {
            try {
                for (int i=0;; i++) {
                    System.out.println("Put de " + i);
                    b.put(i);
                    System.out.println("Put done \n");
                    Thread.sleep(3000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                for (int i=0;; i++) {
                    System.out.println("Get de: " + i);
                    int c = b.get();
                    System.out.println("get retornou " + c);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }
}

public class BoundedBufferMonitors<T> {   // depois, pensar que o Int, poderia ser um T (tipo genérico)

    private int[] buf;
    private int iget = 0;                   // índice onde faço get's
    private int iput = 0;                   // índice onde faço put's

    private int nelems = 0;



    public BoundedBufferMonitors(int N) {
        buf = new int[N];

    }

    public synchronized int get() throws InterruptedException {     // com synchronized, correm em exclusão mútua

        while(nelems == 0){         // while (!nelems > 0) {wait();}
            wait();
        }

        int res;
        res = buf[iget];
        iget = (iget + 1) % buf.length;

        // notify();            ia correr mal!!!
        nelems -= 1;
        notifyAll();
        //if (nelems == buf.length - 1) {             // ???
        //    notifyAll();
        //}

        return res;
    }

    public synchronized void put(int v) throws InterruptedException {   // com synchronized, correm em exclusão mútua

        while(nelems == buf.length){
            wait();
        }

        buf[iput] = v;
        iput = (iput + 1) % buf.length;

        nelems += 1;
        notifyAll();

        //if (nelems == 1) {             // ???
        //    notifyAll();
        //}

    }
}


