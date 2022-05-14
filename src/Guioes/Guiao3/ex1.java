import java.util.Arrays;
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

        public Bank(int nc) {
        }

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

        /*
        ReentrantReadWriteLock l = new ReentrantReadWriteLock();
        Lock rl = l.readLock();
        Lock wl = l.writeLock();
         */

        int lastId = 0;                                 // índice da última conta colocada

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

            l.lock();                    // Poderíamos por o wl.lock();  USO O WRITE LOCK; ADQUIRIDO PARA ESCRITA

            try {
                lastId += 1;
                accounts.put(lastId, c);
                return lastId;
            } finally {
                l.unlock();
            }
        }


        int closeAccount(int id) throws InvalidAccount {
            Account c;
            l.lock();                               // lock do banco
                                                    // Poderíamos por o wl.lock();  USO O WRITE LOCK; ADQUIRIDO PARA ESCRITA

            try {
                c = accounts.get(id);
                if (c == null) throw new InvalidAccount();

                c.l.lock();                         // lock da conta
                accounts.remove(id);

            } finally {
                l.unlock();                         // libertar o lock do banco
            }
            try{
                return c.balance();
            } finally {
                c.l.unlock();
            }

        }


        public void deposit(int id, int val) throws InvalidAccount, NotEnoughFunds {
            Account c;
            l.lock();                                           // rl.lock()

            try{
                c = accounts.get(id);
                if(c==null) throw new InvalidAccount();
                c.l.lock();
            } finally {
                l.unlock();
            }

            try {
                c.deposit(val);
            } finally {
                c.l.unlock();
            }
        }

        /*

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

        // HIPOTESE ERRADA EM BAIXO

        public void deposit(int id, int val) throws InvalidAccount, NotEnoughFunds {
            Account c;
            l.lock();

            try{
                c = accounts.get(id);
                if(c==null) throw new InvalidAccount();
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

        */

        public void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds {
            Account c;
            l.lock();                                               // rl.lock()

            try{
                c = accounts.get(id);
                if(c==null) throw new InvalidAccount();
                c.l.lock();
            } finally {
                l.unlock();
            }
            try{
                c.withdraw(val);
            } finally {
                c.l.unlock();
            }
        }


        public int totalBalance(int[] acs) throws InvalidAccount {
            int total = 0;

            acs = acs.clone();
            Arrays.sort(acs);
            Account[] a = new Account[acs.length];          // array de contas, para guardar

            l.lock();                                       // rl.lock()

            try{
                for(int i=0; i<acs.length; ++i){
                    Account c = accounts.get(acs[i]);
                    if(c==null) throw new InvalidAccount();
                    a[i] = c;                               // para consultarmos mais tarde
                }
                for(Account c : a){                         // se chegamos aqui, estámos livres de exceções !!
                    c.l.lock();                             // lock da conta
                }
            } finally {
                l.unlock();
            }

            for(Account c : a){
                total += c.balance();
                c.l.unlock();                               // unlock da conta
            }

            return total;
        }


        public void transfer(int from, int to, int val) throws InvalidAccount, NotEnoughFunds {

            if (from == to) return;                      // Mesmas contas

            Account cfrom = accounts.get(from);
            Account cto = accounts.get(to);

            Account o1, o2;
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
        final Bank b;

        Transferer(int iterations, Bank b) {
            this.iterations = iterations;
            this.b = b;
        }

        public void run() {
            Random r = new Random();
            for (int i = 0; i < iterations; i++) {
                try {
                    int from = r.nextInt(b.accounts.size());   // no hashMap é size; (e não length)
                    int to = r.nextInt(b.accounts.size());
                    b.transfer(from, to, 1);
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
            this.b = b;
        }

        public void run() {
            for (int i = 0; i < iterations; i++) {
                try {
                    b.deposit(i % b.accounts.size(), 1);       // no hashMap é size; (e não length)
                } catch (InvalidAccount | NotEnoughFunds e) {
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
            } catch (InvalidAccount e) {
                e.printStackTrace();
            }
        }
    }


    class Main {
        public /* static */ void main(String[] args) throws InterruptedException, InvalidAccount, NotEnoughFunds {
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
            new Observer(I, b).start();                      // sempre a observar as threads, concorrentemente

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
