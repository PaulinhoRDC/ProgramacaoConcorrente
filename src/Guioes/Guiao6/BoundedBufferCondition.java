import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Main {
    public static void main(String[] args) throws InterruptedException {
        BoundedBufferCondition b = new BoundedBufferCondition(20);
            new Thread(() -> {
                try {
                    for (int i=0;; i++) {
                        System.out.println("Put de " + i);
                        b.put(i);
                        System.out.println("Put done \n");
                        Thread.sleep(2900);
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
                        Thread.sleep(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

    }
}

public class BoundedBufferCondition {

        private int[] buf;
        private int iget = 0;                   // índice onde faço get's
        private int iput = 0;                   // índice onde faço put's

        private int nelems = 0;

        Lock l = new ReentrantLock();
        Condition notEmpty = l.newCondition();
        Condition notFull = l.newCondition();



        public BoundedBufferCondition(int N) {
            buf = new int[N];

        }

        public int get() throws InterruptedException {
            l.lock();
            try{
                while(nelems == 0){
                    notEmpty.wait();
                }

                int res;
                res = buf[iget];
                iget = (iget + 1) % buf.length;

                nelems -= 1;
                notFull.signal();

                return res;
            } finally {
                l.unlock();
            }

        }

        public void put(int v) throws InterruptedException {
            l.lock();
            try{
                while(nelems == buf.length){
                    notFull.wait();
                }

                buf[iput] = v;
                iput = (iput + 1) % buf.length;

                nelems += 1;
                notEmpty.signal();                 //notifyAll() das que estão em espera por o buffer estar vazio

            } finally {
                l.unlock();
            }


        }
}



