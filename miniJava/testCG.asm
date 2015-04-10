  0         LOADL        0
  1         CALL         L10
  2         HALT   (0)   
  3  L10:   LOADL        1
  4         LOAD         3[LB]
  5         CALL         putintnl
  6         LOADL        2
  7         LOAD         3[LB]
  8         CALL         mult    
  9         LOAD         3[LB]
 10         CALL         add     
 11         LOADL        1
 12         CALL         sub     
 13         STORE        3[LB]
 14         LOAD         3[LB]
 15         CALL         putintnl
 16         LOADL        3
 17         CALL         putintnl
 18         LOAD         3[LB]
 19         LOADL        2
 20         CALL         eq      
 21         JUMPIF (0)   L11
 22         LOADL        4
 23         STORE        3[LB]
 24         JUMP         L12
 25  L11:   LOADL        1
 26         CALL         neg     
 27         STORE        3[LB]
 28  L12:   LOAD         3[LB]
 29         CALL         putintnl
 30         LOADL        0
 31  L13:   LOAD         4[LB]
 32         LOADL        5
 33         CALL         lt      
 34         JUMPIF (0)   L14
 35         LOAD         4[LB]
 36         LOADL        1
 37         CALL         add     
 38         STORE        4[LB]
 39         LOAD         4[LB]
 40         STORE        3[LB]
 41         JUMP         L13
 42  L14:   LOAD         3[LB]
 43         CALL         putintnl
 44         LOADL        -1
 45         LOADL        2
 46         CALL         newobj  
 47         LOADL        5
 48         LOAD         5[LB]
 49         LOADL        0
 50         CALL         fieldref
 51         CALL         add     
 52         STORE        3[LB]
 53         LOAD         5[LB]
 54         LOADL        0
 55         CALL         ne      
 56         JUMPIF (0)   L15
 57         LOAD         3[LB]
 58         LOADL        1
 59         CALL         add     
 60         STORE        3[LB]
 61         JUMP         L15
 62  L15:   LOAD         3[LB]
 63         CALL         putintnl
 64         LOAD         5[LB]
 65         LOADL        1
 66         LOADL        -1
 67         LOADL        2
 68         CALL         newobj  
 69         CALL         fieldupd
 70         LOAD         5[LB]
 71         LOADL        1
 72         CALL         fieldref
 73         LOADL        0
 74         LOADL        7
 75         CALL         fieldupd
 76         LOAD         5[LB]
 77         LOADL        1
 78         CALL         fieldref
 79         LOADL        0
 80         CALL         fieldref
 81         STORE        3[LB]
 82         LOAD         3[LB]
 83         CALL         putintnl
 84         LOAD         5[LB]
 85         LOADL        1
 86         CALL         fieldref
 87         LOADL        1
 88         LOAD         5[LB]
 89         CALL         fieldupd
 90         LOAD         5[LB]
 91         LOADL        1
 92         CALL         fieldref
 93         LOADL        1
 94         CALL         fieldref
 95         LOADL        0
 96         LOAD         3[LB]
 97         LOADL        1
 98         CALL         add     
 99         CALL         fieldupd
100         LOAD         5[LB]
101         LOADL        0
102         CALL         fieldref
103         STORE        3[LB]
104         LOAD         3[LB]
105         CALL         putintnl
106         RETURN (0)   1
