package test.spider.operation;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;

public class GetKeywordTest {
    
    public static void main(String[] args) {
        String text =  "奥迪 奥迪A3 2018款 30周年纪念版 Sportback 35TFSI 进取版";
        
        String text2 = "奥迪 奥迪A3 2018款 30周年年型 Sportback 35 TFSI 进取型";
        /*IkAnalyser(text);
        System.out.println("\r\n");
        IkAnalyser(text2);*/
        /**
         * 奥迪|奥迪|a3|2018款|30周年|纪念版|sportback|35tfsi|进取|版|
         * IkAnalyser
         * 奥迪|奥迪|a3|2018款|30周|年年|型|sportback|35|tfsi|进取|型|
         */
        
        //Segment segment = HanLP.newSegment().enableCustomDictionary(true); 
        //Segment segment = HanLP.newSegment();
        /*CustomDictionary.add("A3");
        CustomDictionary.add("30周年");*/
        
       /* List<Term> seg_1 = segment.seg(text);
        for (Term term : seg_1) {
            System.out.print(term.word+"|");
        }
        
        List<Term> seg_2 = segment.seg(text2);
        for (Term term : seg_2) {
            System.err.print(term.word+"|");
        }*/
        
        //HaNLP(text);
        
        //HaNLP(text2);
        
        keywords(text);
        
        keywords(text2);
        
    }
    
    
    
    public static boolean compareName(String b_name,String a_name) {
        

        return false;
    }
    
    public static void keywords(String text) {
        List<String> extractKeyword = HanLP.extractKeyword(text, 7);
        
        for (String string : extractKeyword) {
            System.err.print(string+"|");
        }
        System.out.println("\r\n");
    }
    
    public static List<String> HaNLP(String text) {
        List<Term> nlptermList = NLPTokenizer.segment(text);
        for (Term term : nlptermList) {
            System.out.print(term.word+"|");
        }
        return null;
    }
    
    /**
     * ik对中文分词支持太差了，废弃
     * @param text
     * @return
     */
    @Deprecated
    public static List<String> IkAnalyser(String text) {
        List<String> list = new ArrayList<>();
        try {
            StringReader b_re = new StringReader(text);
            IKSegmenter ik = new IKSegmenter(b_re,true);
            Lexeme lex = null;
            while((lex=ik.next())!=null){
                System.out.print(lex.getLexemeText()+"|");
                list.add(lex.getLexemeText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}
