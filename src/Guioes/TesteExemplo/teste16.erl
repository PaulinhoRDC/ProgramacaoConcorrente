-module(teste16).
-export([start/0, sinaliza/2, votos/3]).

start() ->
    spawn(fun() -> espera (0,0,0) end).

espera(valor, n1, n2) -> 
    if
        valor =:= 1 ->
            io:format("CHEGAMOS AO QUE É PRETENDIDO~n",[]);
        true -> 
            io:format("AINDA NÃO ESTÁ CERTO ~n",[])
    end,

    receive
        {sinaliza, a, b} ->
                espera(0, a, b);
        {T1,T2} ->
            Res = if
                T1 == n1 andalso T2 == n2 -> 1;
                true -> 0
            end,
        espera(Res,n1,n2)
end.

sinaliza(a, b) ->
    espera ! {sinaliza,a,b}.


votos(T1,T2,tipo) ->
    io:format("Sistema votos~n",[]),
    if 
        tipo =:= tipo1 ->
            espera ! {T1+1,T2};
        true ->
            espera ! {T1, T2+1}
    end.


   





