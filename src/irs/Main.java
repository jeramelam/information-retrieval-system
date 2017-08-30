package irs;

import java.util.ArrayList;
import java.util.List;

public class Main
{
    public static void main(String[] args) throws Exception
    {
    	//List<WebDoc> wList = new ArrayList<WebDoc>();
		PreProcess.main(null);
    	List<WebDoc> wList = DataPrep.main(null);
    	List<WebDoc> qList = QueryPrep.main(null);
    	Calculation.calculateRank(wList,qList);
    	System.out.println("---------------------------------------------");
    }

}
