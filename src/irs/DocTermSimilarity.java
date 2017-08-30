package irs;

public class DocTermSimilarity implements Comparable<DocTermSimilarity>
{
    double smlr;
    String docno;
    DocTermSimilarity(String docno, double smlr)
    {
        this.docno = docno;
        this.smlr = smlr;
    }

	@Override
	public int compareTo(DocTermSimilarity smlr) {
		// TODO Auto-generated method stub
		if (this.smlr > smlr.smlr)
        {
            return -1;
        }
        else if (this.smlr < smlr.smlr)
        {
            return 1;
        }
        else
        {
            return 0;
        }
	}
}
