-module(login_manager).
-export([start/0,stop/0,create_account/2,close_account/2,login/2,logout/1,online/0]).

start() -> register(?MODULE,spawn(fun() -> loop(dict:new()) end)).   %inicar o modulo com nome MODULE e um loop 


% Para abstrair o padrão comum das create e close account
call(Request) ->
    ?MODULE ! {Request,self()},          %mandar request ao module com o meu pid
    receive {Res, ?MODULE} -> Res end.   % esperar receber resposta


stop() -> call(stop).

create_account(Username, Passwd) -> call({create_account,Username,Passwd}).
%create_account(User,Pass) -> 
%    ?MODULE ! {create_account, User, Pass, self()}   %mandar mensagem ao module com a funcao que quero e o meu pid
%    receive {Res, ?MODULE} -> Res end.

close_account(Username, Passwd) -> call({close_account,Username,Passwd}).
%close_account(Username, Passwd) ->
%    ?MODULE ! {close_account, User, Pass, self()}   %mandar mensagem ao module com a funcao que quero e o meu pid
%    receive {Res, ?MODULE} -> Res end.

login(Username, Passwd) -> call({login,Username,Passwd}).
%login(User,Pass) ->
%    ?MODULE ! {login, User, Pass, self()}   %mandar mensagem ao module com a funcao que quero e o meu pid
%    receive {Res, ?MODULE} -> Res end.

logout(Username) -> call({logout,Username}).

online() -> call(online).


loop (Map) ->
    receive
        {create_account, User, Pass, From}->                    %from é o pid de quem mandou o pedido
            case dict:find(User,Map) of
                error ->
                    From ! {ok, ?MODULE},                       % mandar mensagem ao from a dizer ok
                    loop(dict:store(User,{Pass,false}, Map));   %faz um loop com o dicionario utilizado, com o user
                                                                %com palavra pass (Ver documentacao do dict em erlang) 
                _ ->
                    From ! {user_exists, ?MODULE},              % mandar mensagem ao from a dizer user_exists
                    loop(Map)                                   %chama esta funcao (continua a fazer o que estava
            end;

        {close_account, User, Pass, From}->
            case dict:find(User,Map) of
                {ok, {Pass, _}} ->                        % _ -> uma coisa qualquer é onde diz se esta online (T/F)
                    From ! {ok, ?MODULE},
                    loop(dict:erase(User,Map));
                _ -> 
                    From ! {invalid, ?MODULE},
                    loop(Map)                                  %transito para este mesmo estado
            end;

        {{login,Username,Passwd},From} ->
            case dict:find(Username, Map) of
                {ok,{Passwd,_}} ->
                    From ! {ok,?MODULE},
                    loop(dict:store(Username,{Passwd,true},Map));
                _ ->
                    From ! {invalid,?MODULE},
                    loop(Map)
            end;

        {{logout,Username},From} ->
            case dict:find(Username, Map) of
                {ok,{Passwd,_}} ->
                    From ! {ok,?MODULE},
                    loop(dict:store(Username,{Passwd,false},Map));
                _ ->
                    From ! {ok,?MODULE},
                    loop(Map)
            end;

        {online,From} ->
            From ! {[Username || {Username,{_,true}}<-dict:to_list(Map)],?MODULE},
            loop(Map);

        {stop,From} ->
            From ! {ok,?MODULE}
end.                                            