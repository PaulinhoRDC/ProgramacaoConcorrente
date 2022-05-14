%%%%%%%%
%%% 2. Escreva um módulo que implemente um tipo abstracto de dados priorityqueue, 
%%% que generalize uma queue introduzindo a noção de prioridade: 
%%% um dequeue deve remover o item mais antigo da classe com maior prioridade. 
%%% Deverão ser disponibilizadas as funções:
%%% create() -> PriQueue
%%% enqueue(PriQueue, Item, Priority) -> PriQueue
%%% dequeue(PriQueue) -> empty | {PriQueue, Item}1
%%%%%%%%

-module(ex2).
-export([create/0,enqueue/2,dequeue/1,test/0]).


create() -> {[],[]}.

enqueue({In,Out}, Item) -> {[Item|In],Out}.

dequeue({[],[]}) -> empty;
dequeue({In,[Item|Rest]}) -> {{in,Rest},Item};
dequeue({In,[]}) -> dequeue({[],lists:reverse(In)}).

test() ->
    L1=create(),
    L2=enqueue(L1,1),
    L3=enqueue(L2,2),
    L4=enqueue(L3,3),
    L5=enqueue(L4,4),
    L6=enqueue(L5,5),
    L7=enqueue(L6,6),
    L8=enqueue(L7,7),
    L9=enqueue(L8,8),
    {L10,1}=dequeue(L9),
    {L11,2}=dequeue(L10),
    {L12,3}=dequeue(L11),
    {L13,4}=dequeue(L12),
    {L14,5}=dequeue(L13),
    {_,6}=dequeue(L14),
    empty=dequeue(L1),
    ok.