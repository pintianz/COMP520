  0         LOADL        0
  1         PUSH         1
  2         CALL         L10
  3         POP          1
  4         HALT   (0)   
  5  L10:   LOADL        -1
  6         LOADL        1
  7         CALL         newobj  
  8         LOAD         3[LB]
  9         LOADL        0
 10         LOADL        5
 11         CALL         neg     
 12         CALL         fieldupd
 13         LOAD         3[LB]
 14         CALLI        L14
 15         JUMPIF (1)   L11
 16         LOADL        0
 17         LOAD         3[LB]
 18         CALLI        L15
 19         CALL         or      
 20         JUMP         L12
 21  L11:   LOADL        1
 22  L12:   JUMPIF (0)   L13
 23         LOAD         3[LB]
 24         LOADL        0
 25         CALL         fieldref
 26         CALL         putintnl
 27         POP          0
 28         JUMP         L13
 29  L13:   RETURN (0)   1
 30  L14:   LOADL        6
 31         STORE        0[OB]
 32         LOADL        0
 33         RETURN (1)   0
 34  L15:   LOADL        7
 35         STORE        0[OB]
 36         LOADL        1
 37         RETURN (1)   0
