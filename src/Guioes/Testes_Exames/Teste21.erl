-module(aposta).
-export([start/0,participa/1,advinha/3]).

start() -> spawn(fun() -> initPartida ([]) end).

initPartida(Jogadores) -> 
    receive 
        {participa, Pid} -> 
            NewJogadores = [Pid | Jogadores],
            if 
                length(NewJogadores) =:= 9 -> 
                    io:format("Starting", []),
                    Partida = spawn(fun() -> partida(false,0,0,NewJogadores,0,#{}) end),
                    [Jogador ! {start,Partida} || Jogador <- NewJogadores],
                    initPartida([]);
                true -> 
                    initPartida(NewJogadores)                          
            end
    end.



partida(Acaba,Tentativas,Soma,Jogadores,Aux,Map) -> 
    receive 
        {adivinha,Valor,Guess,Pid} -> 
            maps:put(Pid,Guess,Map),
            if 
                length(Jogadores) =:= Aux -> 
                    sendMessage(Jogadores,Map,Soma+Valor);
                true -> 
                    partida(Acaba,Tentativas,Soma+Valor,Jogadores,Aux+1,Map)  
            end 
    end. 

participa(Servidor) -> 
    Servidor ! {participa,self()},
    receive
        {start,Partida} -> 
            Partida
    end. 



advinha(Partida,N,Guess) -> 
    Partida ! {advinha,N,Guess,self()},
    receive
        {res,Msg} -> 
            io:format("~p ~n", [Msg])
end.


sendMessage([H|T],Map,Soma) -> 
    Val = maps:get(H,Map),
    if 
        Soma ==  Val-> 
            H ! {res,true},
            sendMessage([T],Map,Soma); 
        true -> 
            H ! {res,false},
            sendMessage([T],Map,Soma)
end. 