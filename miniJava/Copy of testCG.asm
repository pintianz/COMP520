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
 14         CALLI        L13
 15         JUMPIF (1)   L11
 16         LOAD         3[LB]
 17         CALLI        L14
 18         CALL         or      
 19  L11:   JUMPIF (0)   L12
 20         LOAD         3[LB]
 21         LOADL        0
 22         CALL         fieldref
 23         CALL         putintnl
 24         POP          0
 25         JUMP         L12
 26  L12:   RETURN (0)   1
 27  L13:   LOADL        6
 28         STORE        0[OB]
 29         LOADL        0
 30         RETURN (1)   0
 31  L14:   LOADL        7
 32         STORE        0[OB]
 33         LOADL        1
 34         RETURN (1)   0
