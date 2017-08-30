package irs;

import java.io.*;
import java.text.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class Calculation
{
	@SuppressWarnings({ "rawtypes" })
	public static void calculateRank(List<WebDoc> wList, List<WebDoc> qList) throws IOException {
        File output = new File("./Ranking.txt");
        FileOutputStream fos = new FileOutputStream(output);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        NumberFormat decimalFormat = new DecimalFormat("0.000000000000000");
        int queryCount = 0;
        for (WebDoc query : qList)
        {
            ArrayList<DocTermSimilarity> similarity = new ArrayList<DocTermSimilarity>();
            for (WebDoc doc : wList)
            {
                Iterator qwc = query.getIndexMap().entrySet().iterator();
                HashMap<String, int[]> keyMap = new HashMap<String, int[]>();
                while (qwc.hasNext())
                {
                    Entry set = (Entry) qwc.next();
                    keyMap.put((String) set.getKey(), new int[]
                    {
                        (Integer) set.getValue(), 0
                    });
                }
                Iterator dwc = doc.getIndexMap().entrySet().iterator();
                while (dwc.hasNext())
                {
                    Entry set = (Entry) dwc.next();
                    if (keyMap.containsKey(set.getKey()))
                    {
                        int a = ((int[]) keyMap.get(set.getKey()))[0];
                        keyMap.put((String) set.getKey(), new int[]
                        {
                            a, (Integer) set.getValue()
                        });
                    }
                    else
                    {
                        keyMap.put((String) set.getKey(), new int[]
                        {
                            0, (Integer) set.getValue()
                        });
                    }
                }
                Iterator convert = keyMap.entrySet().iterator();
                ArrayList<Integer> t1 = new ArrayList<Integer>();
                ArrayList<Integer> t2 = new ArrayList<Integer>();
                while (convert.hasNext())
                {
                    Entry set = (Entry) convert.next();
                    int[] value = (int[]) set.getValue();
                    t1.add(value[0]);
                    t2.add(value[1]);
                }
                double smlr = cosSim(t1, t2); //getting the similarity from two arrays of integers
                similarity.add(new DocTermSimilarity(doc.getDocno(), smlr));
            }
            Collections.sort(similarity);
            int rank = 1;
            for (DocTermSimilarity smlr : similarity.subList(0, 1000))
            {
                bw.write(query.getDocno() + "  Q0  " + smlr.docno + "  " + rank++ + "  " + decimalFormat.format(smlr.smlr) + "  Group003\n");
            }
            System.out.println("Processed query "+queryCount+" ("+query.getDocno()+")");
            queryCount++;
        }
        bw.close();
	}
	
    public static double cosSim(ArrayList<Integer> t1, ArrayList<Integer> t2)
    {
        int numerator = 0;
        double denominatorA = 0d;
        double denominatorB = 0d;
        double denominator = 0d;
        double cosSim = 0d;
        for (int i = 0; i < t2.size(); i++)
        {
            numerator += t1.get(i) * t2.get(i); 
            denominatorA += Math.pow(t1.get(i), 2);
            denominatorB += Math.pow(t2.get(i), 2);
        }
        denominator = Math.sqrt(denominatorA) * Math.sqrt(denominatorB);
        cosSim = numerator / denominator;
        return cosSim;
    }
}
