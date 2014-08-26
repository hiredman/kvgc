# kvgc

A Clojure library designed to provide naive stop the world mark and
sweep garbage collection over a key value store

## Usage

extend the `com.manigfeald.kvgc/Heap` protocol to your key value
store, your heap must also be reducible as key-value pairs of pointers
and some thing else (the value isn't currently used)

call `com.manigfeald.kvgc/gc` with your heap, root-set and mark-state
(a boolean, the initial value doesn't matter), get a [heap mark-state]
back, pass those values back in to the `gc` function next time you
want to gc.

## Why?

Because one day my processes will loose these bonds of physical
machinery, to compute using transcendent nominal mechanics outside the
boundaries of electrical components so cruelly referred to as being
random access, and it seems like having a garbage collector when that
happens would be nice.

## License

Copyright © 2014 Kevin Downey

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
