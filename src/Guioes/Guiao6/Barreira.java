import java.util.concurrent.Semaphore;


class Barreira {

    private int c=0;
    private int e=0;
    private final int N;


    public Barreira(int N){
        this.N=N;
    }
    public synchronized void await() throws InterruptedException{
        c+=1;
        int current_e=e;
        if (c==N){
            notifyAll();
            e+=1;
            c=0;
        }else while (current_e==e)
            wait();
    }
}

class Barreira {

    private int c=0;
    private int out=0;
    private final int N;


    public Barrier(int N){
        this.N=N;
    }
    public synchronized void await() throws InterruptedException{
        while (out<N)
            wait();
        c+=1;
        if (c==N){
            notifyAll();
            out=0;
        }else while (c<N)
            wait();
        out+=1;
        if (out == N){
            c=0;
            notifyAll();
        }
    }
}

class Main{
    public static void main(String[] args) {
        Barreira b= new Barreira(3);
        new Thread(()->{
            try{
                Thread.sleep(100);
                System.out.println("vou fazer await - 1");
                b.await();
                System.out.println("await returnou - 1");
            }catch(Exception e){}
        }).start();

        new Thread(()->{
            try{
                Thread.sleep(100);
                System.out.println("vou fazer await - 2");
                b.await();
                System.out.println("await returnou - 2");
            }catch(Exception e){}
        }).start();

        new Thread(()->{
            try{
                Thread.sleep(100);
                System.out.println("vou fazer await - 3");
                b.await();
                System.out.println("await returnou - 3");
            }catch(Exception e){}
        }).start();
    }
}


