/*

    3. Escreva uma abstracção para permitir que N threads se sincronizem:
                    class Barreira {
                    Barreira (int N) { ... }
                    void await() throws InterruptedException { ... }
                    }
        A operação await deverá bloquear até que as N threads o tenham feito; nesse momento
        o método deverá retornar em cada thread.
            a) Suponha que cada thread apenas vai invocar await uma vez sobre o objecto.
            b) Modifique a implementação para permitir que a operação possa ser usada várias vezes por cada thread
           (barreira reutilizável), de modo a suportar a sincronização no fim de cada uma de várias fases de computação.

     */

    class Main {
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
                    Thread.sleep(1000);
                    System.out.println("vou fazer await - 2");
                    b.await();
                    System.out.println("await returnou - 2");
                }catch(Exception e){}
            }).start();
    
            new Thread(()->{
                try{
                    Thread.sleep(2500);
                    System.out.println("vou fazer await - 3");
                    b.await();
                    //Thread.sleep(2500);
                    System.out.println("await returnou - 3");
                }catch(Exception e){}
            }).start();
        }     
    }

public class Barreira {

    private final int N;
    private int c = 0;

    private boolean w = false;
    private boolean awaking = false;

    public Barreira(int N) { this.N = N; }

    public synchronized void await() throws InterruptedException {
        c += 1;

        if (c == 1) {
            w = true;
        }
        if (c == N) {
            notifyAll();
            awaking = true;
            //c = 0;                                      // reset ao contador
            w = false;                                  // já não é preciso esperar
        }
        while (w){
            wait();
        }

        c-=1;
        if(c == 0){
            awaking = false;
        }

    }
}



/*

public class Barreira {


    private final int N;
    private int c = 0;
    private int e = 0;

    public Barreira(int N) { this.N = N; }

    public synchronized void await() throws InterruptedException {
        int e_snapshot = e;
        c += 1;

        if (c == N) {
            notifyAll();
            c = 0;                                      // reset ao contador
            e += 1;
        }
        else while(e == e_snapshot){
        wait();
        }
    }
}

 */

/*

public class Barreira {

    class Instance { int c = 0; }

    private final int N;
    private Instance e = new Instance();

    public Barreira(int N) { this.N = N; }

    public synchronized Instance await() throws InterruptedException {
        Instance e_snapshot = e;
        e.c += 1;

        if (e.c == N) {
            notifyAll();
            e.c = 0;                                      // reset ao contador
            e = new Instance();
        }
        else while(e == e_snapshot){
        wait();
        }
        return e_snapshot;
    }
}

 */
