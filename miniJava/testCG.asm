  0         LOADL        0
  1         PUSH         1
  2         PUSH         1
  3         PUSH         1
  4         CALL         L10
  5         POP          3
  6         HALT   (0)   
  7  L10:   LOADL        11
  8         STORE        1[SB]
  9         LOAD         1[SB]
 10         CALL         putintnl
 11         LOAD         1[SB]
 12         LOADL        55
 13         CALL         add     
 14         STORE        2[SB]
 15         LOAD         2[SB]
 16         CALL         putintnl
 17         LOAD         2[SB]
 18         LOADL        55
 19         CALL         add     
 20         STORE        1[SB]
 21         LOAD         1[SB]
 22         CALL         L18
 23         LOADL        1
 24         LOAD         3[LB]
 25         CALL         putintnl
 26         LOADL        2
 27         LOAD         3[LB]
 28         CALL         mult    
 29         LOAD         3[LB]
 30         CALL         add     
 31         LOADL        1
 32         CALL         sub     
 33         STORE        3[LB]
 34         LOAD         3[LB]
 35         CALL         putintnl
 36         LOADL        3
 37         CALL         putintnl
 38         LOAD         3[LB]
 39         LOADL        2
 40         CALL         eq      
 41         JUMPIF (0)   L11
 42         LOADL        4
 43         STORE        3[LB]
 44         JUMP         L12
 45  L11:   LOADL        1
 46         CALL         neg     
 47         STORE        3[LB]
 48  L12:   LOAD         3[LB]
 49         CALL         putintnl
 50         LOADL        0
 51  L13:   LOAD         4[LB]
 52         LOADL        5
 53         CALL         lt      
 54         JUMPIF (0)   L14
 55         LOAD         4[LB]
 56         LOADL        1
 57         CALL         add     
 58         STORE        4[LB]
 59         LOAD         4[LB]
 60         STORE        3[LB]
 61         POP          0
 62         JUMP         L13
 63  L14:   LOAD         3[LB]
 64         CALL         putintnl
 65         LOADL        -1
 66         LOADL        3
 67         CALL         newobj  
 68         LOADL        5
 69         LOAD         5[LB]
 70         LOADL        0
 71         CALL         fieldref
 72         CALL         add     
 73         STORE        3[LB]
 74         LOAD         5[LB]
 75         LOADL        0
 76         CALL         ne      
 77         JUMPIF (0)   L15
 78         LOAD         3[LB]
 79         LOADL        1
 80         CALL         add     
 81         STORE        3[LB]
 82         JUMP         L15
 83  L15:   LOAD         3[LB]
 84         CALL         putintnl
 85         LOAD         5[LB]
 86         LOADL        1
 87         LOADL        -1
 88         LOADL        2
 89         CALL         newobj  
 90         CALL         fieldupd
 91         LOAD         5[LB]
 92         LOADL        1
 93         CALL         fieldref
 94         LOADL        0
 95         LOADL        7
 96         CALL         fieldupd
 97         LOAD         5[LB]
 98         LOADL        1
 99         CALL         fieldref
100         LOADL        0
101         CALL         fieldref
102         STORE        3[LB]
103         LOAD         3[LB]
104         CALL         putintnl
105         LOAD         5[LB]
106         LOADL        1
107         CALL         fieldref
108         LOADL        1
109         LOAD         5[LB]
110         CALL         fieldupd
111         LOAD         5[LB]
112         LOADL        1
113         CALL         fieldref
114         LOADL        1
115         CALL         fieldref
116         LOADL        0
117         LOAD         3[LB]
118         LOADL        1
119         CALL         add     
120         CALL         fieldupd
121         LOAD         5[LB]
122         LOADL        0
123         CALL         fieldref
124         STORE        3[LB]
125         LOAD         3[LB]
126         CALL         putintnl
127         LOADL        4
128         LOAD         6[LB]
129         CALL         newarr  
130         LOAD         7[LB]
131         LOADL        -1
132         CALL         add     
133         LOADI  
134         STORE        3[LB]
135         LOADL        2
136         LOAD         3[LB]
137         CALL         mult    
138         LOADL        1
139         CALL         add     
140         CALL         putintnl
141         LOAD         7[LB]
142         LOADL        0
143         LOADL        0
144         CALL         arrayupd
145         LOADL        1
146         STORE        4[LB]
147  L16:   LOAD         4[LB]
148         LOAD         6[LB]
149         CALL         lt      
150         JUMPIF (0)   L17
151         LOAD         7[LB]
152         LOAD         4[LB]
153         LOAD         7[LB]
154         LOAD         4[LB]
155         LOADL        1
156         CALL         sub     
157         CALL         arrayref
158         LOAD         4[LB]
159         CALL         add     
160         CALL         arrayupd
161         LOAD         4[LB]
162         LOADL        1
163         CALL         add     
164         STORE        4[LB]
165         POP          0
166         JUMP         L16
167  L17:   LOAD         7[LB]
168         LOADL        3
169         CALL         arrayref
170         LOADL        4
171         CALL         add     
172         STORE        3[LB]
173         LOAD         3[LB]
174         CALL         putintnl
175         LOADL        1
176         LOADL        2
177         LOADL        3
178         LOAD         5[LB]
179         CALLI        L19
180         LOADL        1111
181         CALL         L18
182         LOAD         5[LB]
183         CALLI        L20
184         LOADL        999
185         CALL         putintnl
186         RETURN (0)   1
187  L18:   LOAD         -1[LB]
188         CALL         putintnl
189         RETURN (0)   1
190  L19:   LOADL        11
191         LOAD         -1[LB]
192         CALL         putintnl
193         RETURN (0)   3
194  L20:   LOADL        11
195         LOAD         3[LB]
196         CALL         putintnl
197         LOADL        12
198         STORE        0[OB]
199         LOAD         1[OB]
200         LOADL        1
201         CALL         fieldref
202         LOADL        0
203         CALL         fieldref
204         STORE        3[LB]
205         LOAD         3[LB]
206         CALL         putintnl
207         LOADL        4
208         STORE        0[OB]
209         LOADL        2
210         LOADL        3
211         LOADL        4
212         LOADA        0[OB]
213         CALLI        L21
214         CALL         add     
215         STORE        3[LB]
216         LOAD         3[LB]
217         CALL         putintnl
218         LOADL        8
219         LOADL        3
220         LOAD         1[OB]
221         CALLI        L23
222         CALL         add     
223         CALL         putintnl
224         LOADA        0[OB]
225         LOADL        0
226         LOADL        4
227         CALL         fieldupd
228         LOAD         1[OB]
229         LOADL        0
230         LOADL        5
231         CALL         fieldupd
232         LOADL        2
233         LOADA        0[OB]
234         LOADA        0[OB]
235         LOADL        1
236         CALL         fieldref
237         LOADA        0[OB]
238         CALLI        L22
239         CALL         add     
240         CALL         putintnl
241         RETURN (0)   0
242  L21:   LOAD         0[OB]
243         LOAD         -2[LB]
244         CALL         add     
245         LOAD         -1[LB]
246         CALL         add     
247         RETURN (1)   2
248  L22:   LOAD         -2[LB]
249         LOADL        0
250         CALL         fieldref
251         LOAD         -1[LB]
252         LOADL        0
253         CALL         fieldref
254         CALL         add     
255         LOADA        0[OB]
256         LOADL        0
257         CALL         fieldref
258         CALL         add     
259         RETURN (1)   2
260  L23:   LOADL        1
261         LOAD         -1[LB]
262         LOADL        1
263         CALL         gt      
264         JUMPIF (0)   L24
265         LOAD         -1[LB]
266         LOAD         -1[LB]
267         LOADL        1
268         CALL         sub     
269         LOADA        0[OB]
270         CALLI        L23
271         CALL         mult    
272         STORE        3[LB]
273         JUMP         L24
274  L24:   LOAD         3[LB]
275         RETURN (1)   1
