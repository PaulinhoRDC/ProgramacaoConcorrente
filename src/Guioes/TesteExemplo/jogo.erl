-module(jogo).
-export([start/0, participa/1, adivinha/2]).

start() -> 
    spawn(fun() -> jogo([]) end).


participa(Jogo) -> 
    Jogo ! {participa, self()},
    receive {Partida, Jogo} -> Partida end.


adivinha(Partida, N) ->
    Partida ! {adivinha, N , self()},             
    receive {Res, Partida} -> Res end.


jogo(Jogadores) when length(Jogadores) =:= 4 ->
    Rand = rand:uniform(100),
    Partida = spawn(fun() -> partida(Rand) end),
    [J ! {Partida, self()} || J <- Jogadores],
    jogo([]);

jogo(Jogadores) ->
    receive 
        {participa, From} -> jogo([From | Jogadores]) end.


partida(Numero) ->
    Self = self(), 
    spawn(fun() -> receive after 60000 -> Self() ! timeout end end),
    partida(false, 0, false, Numero).

partida (Ganhou, Tentativas, Timeout, Numero) -> 
    receive
        {adivinha, N, From} ->
            Res = 
            if
                Ganhou -> "PERDEU";
                Timeout -> "TEMPO";
                Tentativas >= 100 -> "TENTATIVAS";
                N =:= Numero -> "GANHOU";
                true -> "PERDEU"
            end,
            From ! {Res, self()},
            partida(Ganhou orelse Res =:= "GANHOU", Tentativas + 1, Timeout, Numero);
        timeout -> 
            partida(Ganhou, Tentativas, true, Numero)
    end.
