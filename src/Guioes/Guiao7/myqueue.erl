-module(myqueue).
-export([create/0, enqueue/2, dequeue/1, test/0]).

create() -> [].

enqueue(Queue, Item) -> Queue ++ [Item].

dequeue([]) -> empty;
dequeue([H | T]) -> {T, H}.                       % [H | T]        ->        L1 ++ L3

test () ->
    L1 = create(),
    L2 = enqueue(L1,1), % [1]
    L3 = enqueue(L2,2), % [1,2]
    L4 = enqueue(L3,3),
    L5 = enqueue(L4,4),
    L6 = enqueue(L5,5),
    L7 = enqueue(L6,6),
    L8 = enqueue(L7,7),
    L9 = enqueue(L8,8),
    {[2],1} = dequeue(L3),
    empty = dequeue(L1),
    ok.
