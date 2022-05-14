class Main {
    public static void main (String[] args) throws InterruptedException {
        final int N = Integer.parseInt(args[0]);
        final int I = Integer.parseInt(args[1]);

        Counter c = new Counter();                      // criação do objeto contador, para pudermos passar como argumento no printer, para cada Thread ter o seu próprio contador

        Thread[] a = new Thread[N];                     // para guardar as diferentes Threads

        for (int i = 0; i<N; i++){ a[i] = new Printer(I, c); }
        for (int i = 0; i<N; i++){ a[i].start(); }
        for (int i = 0; i<N; i++){ a[i].join();  }

        /*
            for (int i = 0 ; i<N; i++){
                a[i] = new Printer(I,c);
                a[i].start();
                a[i].join();                   IRIA DAR MAL, PORQUE IRIA HAVER CRIAÇÃO E EXECUÇÃO DE THREAD SEQUENCIALMENTE
                                              E, O OBJETIVO, SERÁ HAVER CONCORRÊNCIA DE THREADS!
           }
         */

        System.out.println(c.value);                    // Para mostrar o valor final da Thread
    }
}

class Counter {
    int value=0;
}

class Printer extends Thread {
    final int iterations;
    final Counter c;

    Printer(int iterations, Counter c) {
        this.iterations = iterations;
        this.c = c;
    }

    public void run(){
        for (int i=0; i< iterations; i++){
            c.value +=1 ;                           //Para incrementar o contador
        }
    }
}

/*
paulinhordc@MBP-de-Paulo Guiao1 % javac ex2.java   
paulinhordc@MBP-de-Paulo Guiao1 % java ex2.java 2 2
4
*/