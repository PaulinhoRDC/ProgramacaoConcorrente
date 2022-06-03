import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Escalonador {

    Map<Runnable,Integer> emexecucao = new HashMap<>();
    Lock l = new ReentrantLock();

    synchronized void executa(Runnable tarefa,int prioridade) throws InterruptedException {
        emexecucao.put(tarefa,prioridade);

        if(prioridade == 3){
            notifyAll();
            tarefa.run();
            emexecucao.remove(tarefa,prioridade);
        }
        else{
            if(prioridade==2 && emexecucao.size()<5){
                notifyAll();
                tarefa.run();
                emexecucao.remove(tarefa,prioridade);
            }
            else{
                if(prioridade==1 && emexecucao.size()<5){
                    boolean faz = true;
                    for (Integer t : emexecucao.values()){
                        if(t!=1){
                            faz = false;
                        }
                    }
                    if(faz){
                        notifyAll();
                        tarefa.run();
                        emexecucao.remove(tarefa,prioridade);
                    }
                }
                else{
                    wait();
                }
            }
        }
    }

}