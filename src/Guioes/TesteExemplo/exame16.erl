-module(exame16).
-export([start/0]).

start() -> 
    spawn(fun() -> votos(0,0,0) end).


votos(A, B, C)->
    spawn(fun() -> espera (0,0,0,false) end),
    espera ! {A, B, C},
    receive
        {a} ->
            io:format("Voto para o candidato 1~n"),
            votos(A+1, B, C);
        {b} ->
            io:format("Voto para o candidato 2~n"),
            votos(A,B+1,C);
        {c} ->
            io:format("Voto para o candidato 3~n"),
            votos(A,B,C+1);
        {show} ->
            io:format("A= ~p, B= ~p, C= ~p ~n",[A,B,C]),
            votos(A,B,C)
end.


espera(_,_,_,true) ->
    io:format("Ordem estabelecida~n");
espera(_,_,_,false) ->
    receive
        {A,B,C} ->
            io:format("recebi os votos da votacao ~n",[]),
            Res = 
                if
                    (A < B andalso A < C andalso B < C)-> true;
                    true -> false
                end,
            
            espera(A,B,C,Res)
end.




    % timer:sleep(2000),
    % votos ! {sofia},
    % timer:sleep(2000),
    % votos ! {tiago},
    % timer:sleep(2000),
    % votos ! {tiago},
    % timer:sleep(2000),
    % votos ! {show}.