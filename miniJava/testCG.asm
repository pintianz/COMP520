  0         LOADL        0
  1         PUSH         1
  2         CALL         L10
  3         POP          1
  4         HALT   (0)   
  5  L10:   LOADL        -1
  6         LOADL        1
  7         CALL         newobj  
  8         LOADL        57
  9         LOAD         3[LB]
 10         CALLI        L11
 11         STORE        3[LB]
 12         LOAD         3[LB]
 13         LOADL        0
 14         CALL         fieldref
 15         CALL         putintnl
 16         RETURN (0)   1
 17  L11:   LOADL        -1
 18         LOADL        1
 19         CALL         newobj  
 20         LOAD         3[LB]
 21         LOADL        0
 22         LOAD         -1[LB]
 23         CALL         fieldupd
 24         LOAD         3[LB]
 25         CALLI        L12
 26         LOAD         4[LB]
 27         RETURN (1)   1
 28  L12:   LOADA        0[OB]
 29         RETURN (1)   0
