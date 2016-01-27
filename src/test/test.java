package test;


public class test {

	public static void main(String[] args) {
		
		double x=1;
		for(int i=0;i<60;i++){
			x*=(double)(365-i)/365;
		}
		
		System.out.println((1-x)*100+"%");
		
		
	}

}
