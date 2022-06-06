package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static  final Logger logger= LoggerFactory.getLogger(SensitiveFilter.class);
    private static final String REPLACEMENT="****";
    //根节点
    private  TrieNode rootNode=new TrieNode();

    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        TrieNode tempNode=rootNode;
        int begin=0;
        int position=0;
        StringBuilder sb = new StringBuilder();
        while(position<text.length()){
            char c=text.charAt(position);
            //跳过符号
            if(isSymbol(c)){
                if(tempNode==rootNode){
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            //检查下一级节点
            tempNode=tempNode.getSubNode(c);
            if(tempNode==null){
                sb.append(text.charAt(begin));
                position=++begin;
                tempNode=rootNode;
            }else if(tempNode.isKeyWordEnd()){
                sb.append(REPLACEMENT);
                begin=++position;
                tempNode=rootNode;
            }else {
                position++;
            }
        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }
    private  boolean isSymbol(Character c){
        //0x2E80<c<0x9FFF 东亚文字范围
        return CharUtils.isAsciiAlphanumeric(c) && (c<0x2E80 || c>0x9FFF);
    }
    private void addKeyword(String keyword){
        TrieNode tempNode=rootNode;
        for(int i=0;i<keyword.length();i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode==null){
                subNode=new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            //指向子节点
            tempNode=subNode;
            if(i==keyword.length()-1){
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    @PostConstruct
    public void init(){
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader  =new BufferedReader(new InputStreamReader(is))
        )
        {
            String keyword;
            while ((keyword=reader.readLine())!=null){
                addKeyword(keyword);

            }

        }catch (Exception e){
            logger.error("加载敏感词失败"+e);
        }

    }
    private class TrieNode{
        //关键词结束的标识
        private boolean isKeyWordEnd=false;
        //子节点(下级节点字符)
        private Map<Character,TrieNode> subNodes=new HashMap<>();



        public boolean isKeyWordEnd(){
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        /**
         * 添加子节点
         * @param c
         * @param node
         */
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }

        /***
         * 获取子节点
         * @param c
         * @return
         */
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }

    }
}
