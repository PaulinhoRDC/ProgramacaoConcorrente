package Guioes.Guiao5;

public class Barreira {

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

    private final int N;
    private int c = 0;

    private boolean w = false;

    public Barreira(int N) { this.N = N; }

    public synchronized void await() throws InterruptedException {
        c += 1;

        if (c == 1) {
            w = true;
        }
        if (c == N) {
            notifyAll();
            c = 0;                                      // reset ao contador
            w = false;                                  // já não é preciso esperar
        }
        while (w){
            wait();
        }

    }
}
