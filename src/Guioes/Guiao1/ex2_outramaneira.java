package Guioes.Guiao1;

class Counter3 {
    private int value;
    void increment() {value += 1; }
    int value() {return value; }
}

class Increments extends Thread {
    final int iterations;
    final Counter3 c;

    Increments(int iterations, Counter3 c) {
        this.iterations = iterations;
        this.c = c;
    }

    public void run(){
        for (int i=0; i< iterations; i++){
            c.increment();                          //Para incrementar o contador
        }
    }
}

class Main3 {
    public static void main (String[] args) throws InterruptedException {
        final int N = Integer.parseInt(args[0]);
        final int I = Integer.parseInt(args[1]);

        Counter3 c = new Counter3();                      // criação do objeto contador, para pudermos passar como argumento no printer, para cada Thread ter o seu próprio contador

        Thread[] a = new Thread[N];                     // para guardar as diferentes Threads

        for (int i = 0; i<N; i++){ a[i] = new Increments(I, c); }
        for (int i = 0; i<N; i++){ a[i].start(); }
        for (int i = 0; i<N; i++){ a[i].join();  }

        System.out.println(c.value());                    // Para mostrar o valor final da Thread
    }
}

