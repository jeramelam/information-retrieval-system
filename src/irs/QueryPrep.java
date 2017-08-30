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

public class QueryPrep
{
    public static List<WebDoc> main(String[] args) throws Exception
    {
    	List<WebDoc> qList = new ArrayList<WebDoc>();
    	File indexFolder = new File("./query_index");
        if (!indexFolder.exists())
        {
            indexFolder.mkdir();
        }
    	File output = new File("./query_index/index.txt");
		FileOutputStream fos = new FileOutputStream(output);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
    	String qset = FileUtils.readFileToString(new File("QueryTopics.txt"));
		Document doc = Jsoup.parse(qset);
		for (Element e: doc.getElementsByTag("top")) { 
			WebDoc wd = new WebDoc();
			String id = e.getElementsByTag("num").text().substring(8,11);
			wd.setDocno(id);
			String topic = e.getElementsByTag("title").text();
			int index = e.getElementsByTag("desc").text().indexOf("Narrative");
			String desc = e.getElementsByTag("desc").text().substring(13, index);
			String narr = e.getElementsByTag("desc").text().substring(index+11);
			String qText = desc + " " + narr;
			 String[] sText = qText.split("[\\W^\\d]+");
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
            bw.write(id +" "+ topic);
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
 			for (Entry<String, Integer> entry : countIndex.entrySet()) {
 		        if (line.equals("")){
     				line = entry.getKey()+" "+entry.getValue();
 		        } else {
     				line = line + ", " + entry.getKey()+" "+entry.getValue();
 		        }
             }
 			bw.write(" ("+line+")\n");
 			qList.add(wd);
 			System.out.println("...query index...\n");
		}
		bw.close();
		return qList;
    }
}
