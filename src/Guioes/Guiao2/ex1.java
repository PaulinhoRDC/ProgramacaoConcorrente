class Main {
    public static void main (String[] args) throws InterruptedException {
        final int N = Integer.parseInt(args[0]);
        final int I = Integer.parseInt(args[1]);

        Counter c = new Counter();                      // criação do objeto contador, para pudermos passar como argumento no printer, para cada Thread ter o seu próprio contador

        Thread[] a = new Thread[N];                       // para guardar as diferentes Threads

        for (int i = 0; i<N; i++){ a[i] = new Increments(I, c); }
        for (int i = 0; i<N; i++){ a[i].start(); }
        for (int i = 0; i<N; i++){ a[i].join();  }

        System.out.println(c.value());                    // Para mostrar o valor final da Thread
    }
}

class Counter {
    private int value;
    synchronized void increment() {value += 1; }      // CORREÇÃO //
    synchronized int value() {return value; }         // CORREÇÃO //

    // Ou seja, colocar todos os métodos possivelmente usáveis, como synchronized !
}

class Increments extends Thread {
    final int iterations;
    final Counter c;

    Increments(int iterations, Counter c) {
        this.iterations = iterations;
        this.c = c;
    }

    public void run() {                                 // Assim teríamos execução sequencial, isto é, thread a thread
        for (int i = 0; i < iterations; i++) {
            c.increment();                          //Para incrementar o contador
        }        
    }
}
    /*
    public void run() {                                 // Assim teríamos execução sequencial, isto é, thread a thread
        synchronized (c) {
            for (int i = 0; i < iterations; i++) {
                c.increment();                          //Para incrementar o contador
            }
        }
    }

    */

    /*
    public void run() {
        for (int i = 0; i < iterations; i++) {
            synchronized (c) {                          // Desta forma, cada thread teria de ter o lock para atuar
                c.increment();
            }
        }
    }
     */

