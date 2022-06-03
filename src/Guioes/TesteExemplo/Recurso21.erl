-module(recurso21). 
-module([start/0,executa/3]).

start() -> 
    spawn(fun () -> initTarefa(#{}) end).

executa(Tarefa,Server,Prioridade) -> 
    Server ! {tarefa,Tarefa,Prioridade,self()}.

initTarefa(Map) -> 
    receive
        {tarefa, Nome,Prioridade,Pid} -> 
            Tarefas = [Nome | Tarefas],
            maps:put(Nome,Prioridade,Map),
            if
                Prioridade == 3 -> 
                    io:format("~p",[Nome]); 
                    maps:remove(Nome,Map);
                    initTarefa(Map); 

                Prioridade == 2 ->
                    if 
                        maps:size(Map) < 5-> 
                            io:format("~p",[Nome]); 
                            maps:remove(Nome,Map);
                            initTarefa(Map); 
                        true -> 
                            initTarefa(Map); 
                    end

                true -> 
                    if 
                        maps:size(Map) <5 -> 
                            loop(Map,Tarefas,Pid,Nome)
                    end
            end,
        {executa,Tarefa} -> 
            io:fwrite(Tarefa)
    end.

loop(Map,[],Pid,Nome) -> Pid ! {executa,Nome}.

loop(Map,[H|T],Pid,Nome) -> 
    Val = maps:find(H,Map), 
    if 
        Val == 1 -> 
            loop(Map,T,Pid,Nome)
    end. 