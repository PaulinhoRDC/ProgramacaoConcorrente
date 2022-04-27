-module(mymath).
-export([factorial/1]).

factorial(0) -> 1;
factorial(N) -> N * factorial(N-1).




%%%%%%%%%%%%%%%%%%%%%%
% >> erl                      // entrar no erlang
% >> erlc mymath.erl
% >> ls mymath.*
%
% >> c(mymath).               // para carregar de novo
%
% >> mymath:factorial(3).
%
%
% >> q().                     // sair do erlang
% >> control + c              // sair à bruto
%
%
% >> control + g              // para ver shell's
% >> control + h              // listar os comandos que existem

%%%%%%%%%%%%%%%%%%%%
