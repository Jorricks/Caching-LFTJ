# Caching Leapfrog Triejoin

Provided here is an implementation of the Leapfrog Triejoin algorithms which was introduced in [1]
followed by a caching implementation for the counting cache algorithm which was mentioned in [2].

## Algorithms
The Leapfrog Triejoin algorithm mentioned in [1] enables fast joins between datasets for faster querying.
However, when using large datasets it didn't reach it maximum potential because of all the IO's to stable memory.
Therefore the flexible caching was introduced in [2].
Our implementation is such that the simulations are all in main memory, where we create a "cache" which marks
which information would be cached by a real caching implementation. The implementation furthermore counts the amount of
times a specific data item is already in cache and the amount of time it is not. With this information we tried to verify
the results which were reported in [2]

## Credits
The implementation was created by Lisette Sanchez, Brent van Striem and Jorrick Sleijster as part of an assignment for
2IMW20 - Database technology at the Technical University of Technology Eindhoven.


## Reference
[1] Todd L Veldhuizen. Leapfrog triejoin: a worst-case optimal join algorithm. arXiv:1210.0481, 2012.

[2] Oren Kalinsky, Yoav Etsion, Benny Kimelfeld. Flexible Caching in Trie Joins. arXiv:1602.08721, 2016.
