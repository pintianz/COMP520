  0         LOADL        0
  1         CALL         L11
  2         HALT   (0)   
  3  L10:   LOAD         0[OB]
  4         LOAD         -1[LB]
  5         CALL         add     
  6         STORE        0[OB]
  7         RETURN (0)   1
  8  L11:   LOADL        -1
  9         LOADL        1
 10         CALL         newobj  
 11         LOADL        3
 12         LOAD         3[LB]
 13         CALLI        L10
 14         LOAD         3[LB]
 15         LOADL        0
 16         CALL         fieldref
 17         CALL         putintnl
 18         RETURN (0)   1
