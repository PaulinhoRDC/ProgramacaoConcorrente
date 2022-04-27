-module(myqueue).
-export([create/0, enqueue/2, dequeue/1]).

create() -> [].

enqueue(Queue, Item) -> Queue ++ [Item].

dequeue([]) -> empty;
dequeue([H | T]) -> {T, H}.                       % [H | T]        ->        L1 ++ L3

