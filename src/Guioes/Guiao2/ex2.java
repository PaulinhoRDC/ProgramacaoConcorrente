package Guioes.Guiao2;

import java.util.Random;

class NotEnoughFunds extends  Exception{}
class InvalidAccount extends  Exception{}


/*

2. Implemente uma classe Banco que ofereça os métodos da interface abaixo, para crédito, débito e consulta
do saldo total de um conjunto de contas.
Considere um número fixo de contas, definido no construtor do Banco, com saldo inicial nulo.
Utilize exclusão mútua ao nível do objecto Banco.

interface Bank {
    void deposit(int id, int val) throws InvalidAccount;
    void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds;
    int totalBalance(int accounts[]) throws InvalidAccount;
}

 */

class Bank {

    public static class Account {
        int balance;

        //ou então ,
        public /* synchronized */ int balance() {             // COM ESTES SYNCHRONIZED, HÁ CONCORRÊNCIA !!!
            return balance;
        }

        public /* synchronized */ void deposit(int val) {    // COM ESTES SYNCHRONIZED, HÁ CONCORRÊNCIA !!!
            balance += val;
        }

        public /* synchronized */ void withdraw(int val) throws NotEnoughFunds {      // COM ESTES SYNCHRONIZED, HÁ CONCORRÊNCIA !!!
            if (balance < val) throw new NotEnoughFunds();
            balance -= val;
        }

    }

    public Account [] accounts;

    private Account get(int id) throws InvalidAccount {
        if (id < 0 || id >= accounts.length) throw new InvalidAccount() ;
        return accounts[id];
    }

    public Bank(int n) {
        accounts = new Account[n];
        for (int i = 0; i < accounts.length; i++){
            accounts[i] = new Account();
        }
    }

    public /* synchronized */ void deposit(int id, int val) throws InvalidAccount {
        // accounts[id].deposit((val));

        Account c = get(id);
        /* synchronized (this) {    */         // só concorrência a nível do banco

        //c.deposit(val);
        synchronized (c) {
            c.deposit(val);
        }
    }
    public /* synchronized */ void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds {
        Account c = get(id);
        /* synchronized  (this) {  */            // só concorrência a nível do banco

        //c.withdraw(val);

        synchronized (c){                   // BANCO CONTROLA A CONCORRÊNCIA DAS CONTAS
            c.withdraw(val);
        }
    }


    public /* synchronized */ int totalBalance(int[] accounts) throws InvalidAccount {
        int total = 0;
        for (int id :accounts) {
            total += get(id).balance();
        }
        return total;
    }

    //EXERCÍCIO 3//

    public /* synchronized */ void transfer(int from, int to, int val) throws InvalidAccount, NotEnoughFunds {

        if(from == to) return;                      // Mesmas contas

        Account cfrom = get(from);
        Account cto = get(to);

        Account o1,o2;
        if (from<to){
            o1 = cfrom;
            o2 = cto;
        } else{
            o1 = cto;
            o2 = cfrom;
        }

        synchronized (o1){
            synchronized (o2){
                cfrom.withdraw(val);
                cto.deposit(val);           // call dos métodos sobre os objetos !!!
                                            // pois, já os obtemos.
            }
        }
        /*
        withdraw(from,val);
        deposit(from,val);               // como temos synchronized no método transfer
                                         // resolve o problema, pois fazemos sequencialmente
                                         // e não, concorrentemente !!!
                                         // SO HAVIA UMA THREAD DE CADA VEZ, SEM CONCORRÊNCIA !!!

         */

    }
}

class Transferer extends Thread{
    final int iterations;
    final Bank b;

    Transferer(int iterations, Bank b) {
        this.iterations = iterations;
        this.b= b;
    }

    public void run(){
        Random r = new Random();
        for (int i = 0; i < iterations ; i++){
            try {
                int from = r.nextInt(b.accounts.length);
                int to = r.nextInt(b.accounts.length);
                b.transfer(from,to,1);
            } catch (InvalidAccount | NotEnoughFunds e) {
                e.printStackTrace();
            }
        }
    }
}


class Depositor extends Thread {
    final int iterations;
    final Bank b;

    Depositor(int iterations, Bank b) {
        this.iterations = iterations;
        this.b= b;
    }

    public void run(){
        for (int i = 0; i < iterations ; i++){
            try {
                b.deposit( i % b.accounts.length, 1);
            } catch (InvalidAccount e) {
                e.printStackTrace();
            }
        }
    }
}

class Observer extends Thread {
    final int iterations;
    final Bank b;

    Observer(int iterations, Bank b) {
        this.iterations = iterations;
        this.b= b;
    }

    public void run(){
            try {
                int NC = 10;                        // 10 contas
                int[] todasContas = new int [NC];
                for(int i=0; i<iterations; i++){
                    int balance = b.totalBalance(todasContas);
                    if(balance != NC * 1000000){                // 10 contas
                        System.out.println("Saldo errado: " + balance);
                    }
                }
            } catch (InvalidAccount e) {
                e.printStackTrace();
            }
    }
}


class Main5 {
    public static void main(String[] args) throws InterruptedException, InvalidAccount {
        final int N = Integer.parseInt(args[0]);
        final int NC = Integer.parseInt(args[1]);
        final int I = Integer.parseInt(args[2]);

        Bank b = new Bank(NC);
        Thread[] a = new Thread[N];                       // para guardar as diferentes Threads

        int[] todasContas = new int[NC];

        for (int i = 0; i < NC; i++) {
            todasContas[i] = i;
        }
        for (int i = 0; i < NC; i++) {
            b.deposit(i, 1000000);
        }

            // for (int i=0; i<NC; i++) {b.deposit(i,1000); }           // em comentário, para começarmos com slados a 0

            // for (int i = 0; i<N; i++){ a[i] = new Depositor(I, b); }
            for (int i = 0; i < N; i++) {
                a[i] = new Transferer(I, b);
            }
            new Observer(I,b).start();                      // sempre a observar as threads, concorrentemente

            for (int i = 0; i < N; i++) {
                a[i].start();
            }
            for (int i = 0; i < N; i++) {
                a[i].join();
            }

            System.out.println(b.totalBalance(todasContas));                    // Para mostrar o valor final da Thread
    }
}

















