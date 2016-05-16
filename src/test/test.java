package test;


public class test {
static int j=0;
	private static boolean methodB(int k){
		j+=k;
		return true;
	}
	public static void methodA(int i){
		boolean b;
		b=i<10|methodB(4);// 10|true
		System.out.println(b);
		b=i<10||methodB(8);//10||true
		System.out.println(b);
	}

	public static void main(String[] args) {

		methodA(0);
		System.out.println(j);
		
	}

}
