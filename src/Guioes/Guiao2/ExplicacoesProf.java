/*
Devemos fazer:               l.lock()                 l.lock()
                                 -                       -
                                 -                       -
                                 -                       -
                                 -                       -
                            l.unlock()                l.unlock
                                    \                   /
                                     \                 /
                                      \               /
                                       \             /
                                           Objeto
                                         (contador)

Ou será que deveríamos ter uma implementação de um l.lock, mas isso, em cada objeto que temos.
Para que, quem quer entrar no objeto, ter este "entrave" .

                                 l.lock       OBJETO
                                            (contador)

      ------ CASO BÁSICO ------
                                // Em vez de 0, podemos por this, se tivermos código antes ou depois;

synchronized (0) {                                              // como se tivesse, implicitamente, o.lock()
                        ------------
                        ------------
                        ------------
                 }                                              // o.unlock() ,implicitamente.


Ou seja, ficariamos com um lock e unlock, associado a cada um de objeto, para que haja exclusão mútua.

Pode assim até acontecer sairmos do lock, com uma exceção (throw ....)
                                              ou até um return a meio do código ;

Cobre o problema de cada thread ter um lock e unlock, onde podemos não chegar ao unlock;


      ------ CASO COMUM ------

Cabe em termos um synchronized como um método mesmo:

synchronized meth (...) {

                -----
                -----
                -----
    }

            | | |        (EQUIVALENTE)

synchronized (this) {


    }

 !!!!! Mecanismos !!!!!

Método synchronized, que usa o lock de this:
synchronized T m(...) { ... }

Bloco synchronized, que usa o lock de obj:
synchronized (obj) { ... }




*/


public class ExplicacoesProf {
}