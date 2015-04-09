/**************************************************************
 * miniJava PA4 feature test
 *
 *   use this to check contextual analysis and code
 *   generation for successively more adventurous
 *   miniJava constructs.
 *
 *   uncomment System.out.println(x) statements once
 *     you have sufficient functionality
 *   
 *   When interpreted using mJam the compiled code 
 *   should print 1 ... 15 999
 *
 **************************************************************/

/************
 *
 *  mainclass
 */
class PA4Test
{
    public static void main(String[] args)
    {        
        /* 1: simple literal */
        int x = 1;
         System.out.println(x);
        
//        /* 2: simple expression */
//        x = 2 * x + x - 1;
//        // System.out.println(x);
//
//	/* 3: System.out.println */
//	System.out.println(3);
//        
//        /* 4: conditional statement */
//        if (x == 2)
//            x = 4;
//        else
//            x = -1;
//        System.out.println(x);
//        
//        /* 5: repetitive statement */
//        int i = 0;
//        while (i < 5) {
//            i = i + 1;
//            x = i;
//        }
//        System.out.println(x);
//        
//        /* 6: object creation */
//        A a = new A();
//        x = 5 + a.n;
//	if (a != null) 
//	    x = x + 1;
//        System.out.println(x);
//
//        /* 7: object update */
//        a.b = new B();
//        a.b.n = 7;
//        x = a.b.n;
//        System.out.println(x);
//        
//        /* 8: field reference and update */
//        a.b.a = a;
//        a.b.a.n = x + 1;
//        x = a.n;
//        System.out.println(x);
//        
//        /* 9: array creation and length */
//        int aa_length = 4;
//        int [] aa = new int [aa_length];
//	x = aa.length;
//        System.out.println(2*x +1);
//        
//        /* 10: array reference and update */
//        aa[0] = 0;
//	i = 1;
//        while (i < aa_length) {
//            aa[i] = aa[i-1] + i;
//            i = i + 1;
//        }
//        x = aa[3] + 4;
//        System.out.println(x);
//        
//        /* 11: simple method invocation */
//        a.start();
//        
//        /* end of test */
//        System.out.println(999);
    }
}

/**********************************************************************
 *
 *  class A
 */
//class A
//{
//    int n;
//    B b;
//    
//    public void start(){
//        int x = 11;
//        System.out.println(x);
//        
//        /* 12: field ref */
//        n = 12;
//        x = b.a.n;
//        System.out.println(x);
//        
//        /* 13: complex method invocation */
//        n = 4;
//        x = 2 + foo(3,4);
//        System.out.println(x);
//        
//        /* 14: recursion */
//        System.out.println(8 + b.fact(3));
//        
//        /* 15: object-values */
//        this.n = 4;
//        b.n = 5;
//        System.out.println(2 + this.goo(this,this.b));
//        
//    }
//    
//    public int foo(int x, int y) {
//        return (n + x + y);
//    }
//    
//    public int goo(A a, B bb) {
//        return (a.n + bb.n + this.n);
//    }
//}
//
///**********************************************************************
// *
// *  class B
// */
//class B
//{
//    int n;
//    A a;
//    
//    public int fact(int nn){
//        int r = 1;
//        if (nn > 1)
//            r = nn * fact(nn -1);
//        return r;
//    }
//}