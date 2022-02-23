package Guioes.Guiao1;

class Counter2 {
    int value=0;
}

class Printer2 extends Thread {
    final int iterations;
    final Counter2 c;

    Printer2(int iterations, Counter2 c) {
        this.iterations = iterations;
        this.c = c;
    }

    public void run(){
        for (int i=0; i< iterations; i++){
            c.value +=1 ;                           //Para incrementar o contador
        }
    }
}

class Main2 {
    public static void main (String[] args) throws InterruptedException {
        final int N = Integer.parseInt(args[0]);
        final int I = Integer.parseInt(args[1]);

        Counter2 c = new Counter2();                      // criação do objeto contador, para pudermos passar como argumento no printer, para cada Thread ter o seu próprio contador

        Thread[] a = new Thread[N];                     // para guardar as diferentes Threads

        for (int i = 0; i<N; i++){ a[i] = new Printer2(I, c); }
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

