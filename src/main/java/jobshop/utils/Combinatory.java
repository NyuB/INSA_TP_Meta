package jobshop.utils;

public class Combinatory {
	public static long factorial(int n){
		assert n>=0;
		long res=1;
		for(long i=2;i<=n;i++)res*=i;
		return res;
	}

	public static long kInN(int k,int n){
		assert k<=n;
		assert k>=0;
		long nF = 1;//factorial N
		long kF = 1;//factorial K
		long nkF = 1;//factorial(N-K)
		for(long i=2;i<=n;i++){
			if(i<=n-k)nkF*=i;
			if(i<=k)kF*=i;
			nF*=i;
		}
		return nF/(kF*nkF);
	}
}
