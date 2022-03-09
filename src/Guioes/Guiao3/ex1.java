package Guioes.Guiao3;


import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class NotEnoughFunds extends  Exception{}
class InvalidAccount extends  Exception{}




/*

interface Bank {
    int createAccount(int initialBalance);
    int closeAccount(int id) throws InvalidAccount;
    void deposit(int id, int val) throws InvalidAccount;
    void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds;
    void transfer(int from, int to, int amount) throws InvalidAccount, NotEnoughFunds;
    int totalBalance(int accounts[]) throws InvalidAccount;
}

Em que criar uma conta devolve um identificador de conta, para ser usado em outras operações
e fechar uma conta devolve o saldo desta; deverá ainda ser possível obter a soma do
saldo de um conjunto de contas. Algumas operações deverão poder lançar exceções se o
identificador de conta não existir ou não houver saldo suficiente.
Apesar de permitir concorrência, garanta que os resultados sejam equivalentes a ter acontecido
uma operação de cada vez. Por exemplo, ao somar os saldos de um conjunto de contas,
não permita que sejam usados montantes a meio de uma transferência (e.g., depois de retirar
da conta origem e antes de somar à conta destino).

 */

public class ex1 {

    // Objetivo, é ser o próprio banco a ter o controlo;
    // Podemos ter um hashMap, uma variável chamada lastId e sempre que criamos uma conta
    // Incrementa-se a variável e passa a ser este valor o número da conta

    // Para chegarmos às contas, temos de passar pelo banco

    // Termos também uma variável lock, em que o lock protege os objetos


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

            public int balance() {
                return balance;
            }

            public void deposit(int val) {
                balance += val;
            }

            public void withdraw(int val) throws NotEnoughFunds {
                if (balance < val) throw new NotEnoughFunds();
                balance -= val;
            }

            Lock l = new ReentrantLock(); // Para o banco puder utilizar o lock

        }


        HashMap<Integer, Account> accounts = new HashMap<>();
        Lock l = new ReentrantLock();
        int lastId = 0;

            /*
        private Account get(int id) throws InvalidAccount {

            if (id < 0 || id >= accounts.length) throw new InvalidAccount();

             Esta parte, já não faz sentido, no sentido de que agora para aceder às contas,
            temos de passar pelo hashMap ;

            return accounts[id];
        }
             */

        /*
        int createAccount (int balance){

            Account c = new Account();   // FAZER SEM LOCKS ADQUIRIDOS É MELHOR !
            c.deposit(balance);          // FAZER SEM LOCKS ADQUIRIDOS É MELHOR !

            l.lock();
            lastId += 1;
            int id = lastId;

            // Account c = new Account();
            // c.deposit(balance);
            accounts.put(lastId, c);
            l.unlock();

            return id;
        }
         */

        int createAccount (int balance){

            Account c = new Account();   // FAZER SEM LOCKS ADQUIRIDOS É MELHOR !
            c.deposit(balance);          // FAZER SEM LOCKS ADQUIRIDOS É MELHOR !

            l.lock();

            try {
                lastId += 1;
                accounts.put(lastId, c);
                return lastId;
            } finally {
                l.unlock();
            }
        }


        public void deposit(int id, int val) throws InvalidAccount {

            l.lock();

            try {
                Account c = accounts.get(id);
                if (c == null) throw new InvalidAccount();
                c.deposit(val);                             // Se a conta for válida
            } finally {
                l.unlock();                                 // garante que o lock é sempre libertado !!!
            }
        }

        public void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds {
            Account c;
            l.lock();

            try{
                c = accounts.get(id);
                if(c==null) throw new InvalidAccount();
                c.withdraw(val);
            } finally {
                l.unlock();
            }

            c.l.lock();             // DESRESPEITA , POIS TEM UM UNLOCK E DEPOIS FAZEMOS UM LOCK();
            try {
                c.deposit(val);
            } finally {
                c.l.unlock();
            }

        }


            //final da aula AQUIIIIIIII

        public int totalBalance(int[] accounts) throws InvalidAccount {
            int total = 0;
            for (int id : accounts) {
                total += get(id).balance();
            }
            return total;
        }

        //EXERCÍCIO 3//

        public /* synchronized */ void transfer(int from, int to, int val) throws Guioes.Guiao2.InvalidAccount, Guioes.Guiao2.NotEnoughFunds {

            if (from == to) return;                      // Mesmas contas

            Guioes.Guiao2.Bank.Account cfrom = get(from);
            Guioes.Guiao2.Bank.Account cto = get(to);

            Guioes.Guiao2.Bank.Account o1, o2;
            if (from < to) {
                o1 = cfrom;
                o2 = cto;
            } else {
                o1 = cto;
                o2 = cfrom;
            }

            synchronized (o1) {
                synchronized (o2) {
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

    class Transferer extends Thread {
        final int iterations;
        final Guioes.Guiao2.Bank b;

        Transferer(int iterations, Guioes.Guiao2.Bank b) {
            this.iterations = iterations;
            this.b = b;
        }

        public void run() {
            Random r = new Random();
            for (int i = 0; i < iterations; i++) {
                try {
                    int from = r.nextInt(b.accounts.length);
                    int to = r.nextInt(b.accounts.length);
                    b.transfer(from, to, 1);
                } catch (Guioes.Guiao2.InvalidAccount | Guioes.Guiao2.NotEnoughFunds e) {
                    e.printStackTrace();
                }
            }
        }
    }


    class Depositor extends Thread {
        final int iterations;
        final Guioes.Guiao2.Bank b;

        Depositor(int iterations, Guioes.Guiao2.Bank b) {
            this.iterations = iterations;
            this.b = b;
        }

        public void run() {
            for (int i = 0; i < iterations; i++) {
                try {
                    b.deposit(i % b.accounts.length, 1);
                } catch (Guioes.Guiao2.InvalidAccount e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Observer extends Thread {
        final int iterations;
        final Guioes.Guiao2.Bank b;

        Observer(int iterations, Guioes.Guiao2.Bank b) {
            this.iterations = iterations;
            this.b = b;
        }

        public void run() {
            try {
                int NC = 10;                        // 10 contas
                int[] todasContas = new int[NC];
                for (int i = 0; i < iterations; i++) {
                    int balance = b.totalBalance(todasContas);
                    if (balance != NC * 1000000) {                // 10 contas
                        System.out.println("Saldo errado: " + balance);
                    }
                }
            } catch (Guioes.Guiao2.InvalidAccount e) {
                e.printStackTrace();
            }
        }
    }


    class Main5 {
        public static void main(String[] args) throws InterruptedException, Guioes.Guiao2.InvalidAccount {
            final int N = Integer.parseInt(args[0]);
            final int NC = Integer.parseInt(args[1]);
            final int I = Integer.parseInt(args[2]);

            Guioes.Guiao2.Bank b = new Guioes.Guiao2.Bank(NC);
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
                a[i] = new Guioes.Guiao2.Transferer(I, b);
            }
            new Guioes.Guiao2.Observer(I, b).start();                      // sempre a observar as threads, concorrentemente

            for (int i = 0; i < N; i++) {
                a[i].start();
            }
            for (int i = 0; i < N; i++) {
                a[i].join();
            }

            System.out.println(b.totalBalance(todasContas));                    // Para mostrar o valor final da Thread
        }

    }
}
