-module(login_manager).
-export([
    start/0,
    create_account/2,
    close_account/2,
    login/2,
    logout/1,
    online/0
]).

start() -> register(?MODULE, spawn(fun() -> loop(#{}) end)).
% #{}    -> Map

% Para abstrair o padrão comum das create e close account
invoke(Request) ->
    ?MODULE ! {Request, self()},
    receive {Res, ?MODULE} -> Res end.

create_account(User, Pass) -> invoke ({create_account, User, Pass}).
close_account(User, Pass) -> invoke ({close_account, User, Pass}).
login(User, Pass) -> invoke ({login, User, Pass}).
logout(User) -> invoke ({logout, User}).
online() -> invoke (online).

% create_account(User, Pass) ->
%     %envia os dados, self() é o pid, para depois puder receber resposta
%     ?MODULE ! {create_account, User, Pass, self()},
%     receive
%         {Res, ?MODULE} -> Res
%     end.

% close_account(User, Pass) ->
%     ?MODULE ! {close_account, User, Pass, self()},
%     receive
%         {Res, ?MODULE} -> Res
%     end.

loop(Map) ->
    receive 
        {Request, From} ->
            {Res, NewMap} = handle(Request, Map),
            From ! {Res, ?MODULE},
            loop(NewMap)
    end.

handle({create_account, User, Pass}, Map) ->
    case maps:is_key(User, Map) of
        true ->
            {user_exists, Map};
        false ->
            {ok, maps:put(User, {Pass, false}, Map)}
    end;

handle({close_account, User, Pass}, Map) ->
    case maps:find(User, Map) of
        %{ok, {P, _}} when P =:= Pass ->
        {ok, {Pass, _}} ->                          % se receber ok e um Par ( , ) em que o primeiro elemento do par é Pass, partimos para o close_account
            {ok, maps:remove(User, Map)};           
        _ ->
            {invalid, Map}         
    end;

handle(online, Map) ->





%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


% loop(Map) ->
%     receive
%         {{create_account, User, Pass}, From} ->
%             case maps:is_key(User, Map) of
%                 true ->
%                     From ! {user_exists, ?MODULE},
%                     loop(Map);
%                 false ->
%                     From ! {ok, ?MODULE},
%                     loop(maps:put(User, {Pass, false}, Map))
%             end;
%         {{close_account, User, Pass}, From} ->
%             case maps:find(User, Map) of
%                 %{ok, {P, _}} when P =:= Pass ->
%                 {ok, {Pass, _}} ->                          % se receber ok e um Par ( , ) em que o primeiro elemento do par é Pass, partimos para o close_account
%                     From ! {ok, ?MODULE},
%                     loop(maps:remove(User, Map));
%                 _ ->
%                     From ! {invalid, ?MODULE},
%                     loop(Map)
%             end;
%         {{login, User, Pass}, From} ->
%             case maps:find(User, Map) of
%                 {ok, {Pass, _}} ->                          % se receber ok e um Par ( , ) em que o primeiro elemento do par é Pass, partimos para o close_account
%                     From ! {ok, ?MODULE},
%                     loop(maps:put(User, {Pass, true}, Map));
%                 _ ->
%                     From ! {invalid, ?MODULE},
%                     loop(Map)
%             end;
%         {{logout, User}, From} ->
%             case maps:find(User, Map) of
%                 {ok} ->                          
%                     From ! {ok, ?MODULE},
%                     loop(maps:put(User, {Pass, false}, Map));
%                 _ ->
%                     From ! {invalid, ?MODULE},
%                     loop(Map)
%             end;
%         {online, From} ->
%             F = fun (User, {_Pass, true}, Acc) -> [User | Acc];                 % User ++ Acumulador
%                     (_User, _, Acc) -> Acc;            
%                 end,
%             Res = maps:fold(F, [], Map),

%             Res = [U || {U, {_P, true}} <- maps:to_list(Map)],

%             From ! {Res, ?MODULE},
%             loop(Map);  


% end.
