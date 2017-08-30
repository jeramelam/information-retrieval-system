package irs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.terrier.terms.PorterStemmer;

import weka.core.Stopwords;

public class DataPrep
{
	public static List<WebDoc> main(String[] args) throws Exception
    {
    		int num = 0;
    		List<WebDoc> wList = new ArrayList<WebDoc>();
    		File indexFolder = new File("./data_index");
	        if (!indexFolder.exists())
	        {
	            indexFolder.mkdir();
	        }
	        for (File f : new File("./chunks").listFiles()) {
    		File output = new File("./data_index/index"+num+".txt");
    		FileOutputStream fos = new FileOutputStream(output);
    		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
    		String data = FileUtils.readFileToString(f);
    		Document docset = Jsoup.parse(data);
    		int a = 0;
            for (int i = 0; i < docset.getElementsByTag("DOC").size(); i++) {
                String s = data.substring(a, data.indexOf("</DOC>", a) + 6);
                a = data.indexOf("</DOC>", a) + 6;
                Document doc = Jsoup.parse(s);
                for (Element e : doc.getElementsByTag("DOC")) {
                WebDoc wd = new WebDoc();
                String docno = e.getElementsByTag("DOCNO").text();
                wd.setDocno(docno);
                String dText = e.text();
                String[] sText = dText.split("[\\W^\\d]+");
                List<String> wordList = new ArrayList<String>(Arrays.asList(sText));
                for (int j = 0; j < wordList.size(); j++) {
                	if (Stopwords.isStopword(wordList.get(j).toLowerCase())) {
                		wordList.remove(j);
                		j--;
                	} else {
                		PorterStemmer ps = new PorterStemmer();
                		wordList.set(j, ps.stem(wordList.get(j).toLowerCase()));
                	}
                }
                bw.write(wd.getDocno());
    			HashMap<String, Integer> countIndex = new HashMap<String, Integer>();
    			for (String word : wordList) {
    				if (countIndex.get(word)==null) {
    					countIndex.put(word, 1);
    				} else {
    					countIndex.put(word, countIndex.get(word)+1);
    		        }
    		    }
    			wd.setIndexMap(countIndex);
    			String line = "";
    			for (Entry<String, Integer> entry : wd.getIndexMap().entrySet()) {
    		        if (line.equals("")){
        				line = entry.getKey()+" "+entry.getValue();
    		        } else {
        				line = line + ", " + entry.getKey()+" "+entry.getValue();
    		        }
                }
    			bw.write(" ("+line+")\n");
    			wList.add(wd);
		        System.out.println("...data index...\n");
                }
            }
            bw.close();
            num++;
        }
		System.out.println("---finished indexing dataset---");
		return wList;
    }
}
