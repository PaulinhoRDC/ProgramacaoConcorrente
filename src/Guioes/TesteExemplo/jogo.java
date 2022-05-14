import java.util.Random;

interface Jogo {
    Partida participa() throws InterruptedException;
}

interface Partida {
    String adivinha(int n);
}

/*

Não necessitamos de variáveis de condição, isto porque, numa partida não necessitamos de ver o que outras threads fazem ou não.
Não temos de ter threads bloqueadas, à espera de resposta de outras.

- Q: Diferença entre varias v.c. num monitor ou ter vários monitores, cada um com uma v.c. (monitores nativos de Java ( synchronized(), wait(), notify()...) ?

- R: No segundo caso, ao fazer wait nos monitores abaixo, vamos libertar o lock desse monitor, e não do monitor "PRINCIPAL".
     Gera muitas asneiras.
     Basicamente, com predicados que envolvem várias coisas, nós queremos usar v.c todas associadas ao mesmo lock.
     Nos casos em que quando vamos usar uma situação independente, por exemplo, adivinha não necessita de saber o que está a acontecer num jogo,
    isto é, cada partida é independente da interface Jogo.

*/

class JogoImpl implements Jogo {                // ESPÉCIE DE BARREIRA
    PartidaImpl p = new PartidaImpl();
    int jogadores = 0;

    public synchronized Partida participa() throws InterruptedException{
        PartidaImpl ps = p;
        jogadores += 1;                         //ps.jogadores += 1;

        if(jogadores == 4 ){
            notifyAll();                        // acordar as threads em wait(), para estas conseguirem ver que há 4 jogadores;
            jogadores = 0;                      //ps.jogadores = 0;
            ps.start();
            p = new PartidaImpl();
        } else {
            while(p == ps){               // while(ps.jogadores<4), isto se andassemos com ps.jogadores
                wait();
            }
        }
        return ps;
    }
}

/*
Se o número de jogadores de uma partida pudesse alterar, fazia sentido ter um 
                                                                                int jogadores = ...;
Pois a partida necessitava de saber quantos tinha;
E bastava, em cima na JogoImpl ter um   
                                            PartidaImpl ps = p;
                                            ps.jogadores = ...;

                                            return p;               // e devolver ao PartidaImpl, todos os valores necessários

Neste caso, temos um número fixo!
*/

class PartidaImpl implements Partida {  
    int tentativas = 0;
    boolean timeout = false;
    boolean ganhou = false;
    int numero = new Random().nextInt();
    
    void start(){
        new Thread(() -> {
            try{
                Thread.sleep(60000);
                timeout();
            } catch (InterruptedException e) {}        
        }).start();
    }

    synchronized void timeout(){                  // corre em exclusão mútua com adivinha e não vai puder adivinhar mais
        timeout = true;
    }
    
    public synchronized String adivinha(int n){
        if(ganhou) return "PERDEU";
        if(timeout) return "TEMPO";
        if(tentativas >= 100) return "TENTATIVAS"; // já aconteceram 100 tentativas
        if(n == numero) {
            ganhou = true;
            return "GANHOU";
        }

        tentativas += 1;
        return numero > n ? "MAIOR" : "MENOR";

    }

}
