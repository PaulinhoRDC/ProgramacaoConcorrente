-module(login_manager).
-export([start/0.
         create_account/2,
         close_account/2,
         login/2,
         logout/1,
         online/0]).

start() -> spawn(fun() -> loop(#{}) end).                   % #{}    -> Map

create_account() ->