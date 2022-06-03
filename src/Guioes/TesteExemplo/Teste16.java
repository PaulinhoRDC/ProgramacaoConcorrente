import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Teste16 extends Thread {
    public static void main(String[] args) {

        MeuMonitor evts = new MeuMonitor();

        new Thread (() -> {
            try{
                evts.espera(1, 4, 2, 2);
            } catch (Exception e) { }
        }).start();


        new Thread (() -> {
            try {
                System.out.println("vou sinalizar 1 1x");
                evts.sinaliza(1);
                Thread.sleep(3000);

                System.out.println("vou sinalizar 2 1x");
                evts.sinaliza(2);
                Thread.sleep(3000);

                System.out.println("vou sinalizar 1 2x");
                evts.sinaliza(1);
                Thread.sleep(3000);

                System.out.println("vou sinalizar 2 2x");
                evts.sinaliza(2);
                Thread.sleep(3000);

                System.out.println("vou sinalizar 1 3x");
                evts.sinaliza(1);
                Thread.sleep(3000);
                System.out.println("vou sinalizar 1 4x");
                evts.sinaliza(1);
                Thread.sleep(3000);


                
            } catch (Exception e ) { }
        }).start();

    }
}

class MeuMonitor {

    Lock lock = new ReentrantLock();
    Condition notThere = lock.newCondition();
    
    int tipo1;
    int n1;
    int tipo2;
    int n2;

    MeuMonitor() {
        tipo1 = 1;
        tipo2 = 2;
        n1 = 0;
        n2 = 0;
    }


    void sinaliza(int tipo) {
        lock.lock();
        try{
            if (tipo == tipo1) { n1 += 1; }
            if (tipo == tipo2) { n2 += 1; }
            notThere.signalAll();
        } finally {
            lock.unlock();
        }
    }
 
    void espera(int tipo1, int n1, int tipo2, int n2) throws InterruptedException {
        lock.lock();
        try{
            System.out.println("Vou esperar agora...");

            while (!(this.n1 == n1 && this.n2 == n2)) {
                System.out.println("Nao posso sair");
                notThere.await();
            }
            
            System.out.println("Saí da espera");
        } finally {
            lock.unlock();
        }
    }
}