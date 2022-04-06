package Guioes.Guiao6;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
    2. Implemente uma classe Warehouse para permitir a gestão de um armazem acedido concorrentemente.
    Deverão ser disponibilizados os métodos:
            supply(String item, int quantity) – abastecer o armazem com uma dada
        quantidade de um item;
            consume(String[] items) – obter do armazem um conjunto de itens, bloqueando
        enquanto tal não for possível.
*/
public class Warehouse {
    Lock l = new ReentrantLock();

    private /*static*/ class Item {                                 // sem static,
        int quant = 0;
        Condition cond = l.newCondition();
    }

    Map<String, Item> map = new HashMap<>();

    private Item get(String s){
        Item item = map.get(s);
        if(item == null){
            item = new Item();
            map.put(s,item);
        }
        return item;
    }

    public void supply(String s, int quant){
        l.lock();
        try{
            Item item = get(s);
            item.quant += quant;
            item.cond.signalAll();
        } finally {
            l.unlock();
        }
    }

    public void consume(String[] items) throws InterruptedException {
        l.lock();
        try {
            for(String s: items) {
                Item item = get(s);
                while(item.quant == 0){
                    item.cond.await();
                }
                item.quant -= 1;
            }
        } finally {
            l.unlock();
        }
    }

}
