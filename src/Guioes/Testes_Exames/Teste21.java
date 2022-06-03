class Jogo{

    int players = 0;
    Partida partida;
    boolean comeca = false;

    synchronized Partida participa() throws InterruptedException {
        players ++;
        if(players==9 || comeca){
            
            notifyAll();

            partida.start();
            partida.jogadores = players;

            players = 0;
        }
        else{
            if(players>=3){
                new Thread(() -> {
                    try{
                        Thread.sleep(60000);
                        comeca = true;
                    } catch (Exception e){}
                }).start();
            }
            while(players<9 && !comeca){
                wait();
            }
        }
        return partida;
    }
}

class Partida{

    int valor = 0;
    int jogadores = 0;
    int contador = 0;
    int tentativas = 0;

    synchronized boolean aposta(int n,int soma) throws InterruptedException {
        valor += n;
        contador ++;
        boolean ret = false;
        if(jogadores==contador && tentativas==0){
            notifyAll();
            ret = (valor == soma);
        }
        else{
            while(jogadores != contador){
                wait();
            }
        }
        return ret;
    }
}
