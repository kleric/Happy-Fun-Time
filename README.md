Happy-Fun-Time
==============

Shamir's Secret Share Problem Solver. The name is entirely irrelevant. Fetches the keys from the Spreadsheet with all the keys, then attempts to solve from those keys.

The Spreadsheet of keys can be found [HERE](https://docs.google.com/a/kleric.org/spreadsheet/ccc?key=0AnN-5p9SwIfUdHl4Rl9lRGg5VjRoR1pISVdMLWk5TGc#gid=0).

- - -
Rahul has also implemented a "Playground" which allows a multitude of actions.
 - solve
 - fsolve 
 - encode
 - decode
 - join 
 - split
 - prime 

solve
-----
Syntax is:

    solve

Attempts to solve the puzzle using the keys found on the Spreadsheet. This requires that there be 50+ keys available or else it really won't do anything. If you want to get a "solution" (it'll basically be garbage) you can force a solve using `fsolve`.

This is probably what you'll use in order to solve the puzzle!

fsolve
------
Syntax is:

    fsolve

Does the same thing as solve, but spits out a solution even when there are less than 50 keys. 

encode
------
Syntax is:

    encode `a string`
    
Encodes a string into numbers. Should be alphanumerical and without spaces. Splits up the string into pairs of numbers representing each character.
decode
------
Syntax is:

    decode 'a number'

Decodes a number into a string. For example, `1819` would decode into `hi`. It splits up the numbers into pairs (e.g. 18 and 19) then turns them into characters based on the 10 being a, 11, being b, etc.

join
----
Syntax is:

    join PRIME,PA,IR1,PA,IR2,PA,IR3 ...
    
Spits out the secret given the prime and the keys. 

split
-----
Syntax is:

    split PRIME,SECRET,N,K

Spits out the keys given a prime and the secret. `N` is defined as the number of shares you'd like to create, whereas `K` is the number of shares required to get the secret back out.

prime
-----
Syntax is:

    prime BIT_LENGTH

Spits out a `probable prime` of the specified BIT_LENGTH. It's probably going to be prime :P.
