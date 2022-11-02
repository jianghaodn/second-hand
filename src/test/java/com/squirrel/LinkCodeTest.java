package com.squirrel;


import org.junit.Test;

import javax.xml.stream.events.Characters;
import java.util.*;
import java.util.stream.Collectors;

public class LinkCodeTest {

    @Test
    public void test(){
        String letter = "thisisaletter";
        //直接将字符串转化为list集合
        List<String> list = Arrays.asList(letter.split(""));
        Collections.sort(list, Comparator.comparingInt(l -> (int) l.toLowerCase().charAt(0)));
        StringBuilder sb = new StringBuilder();
        list.stream().peek(sb::append).collect(Collectors.toList());
        System.out.println(sb);
    }

    @Test
    public void testRegex(){
        String s = "123";
        boolean matches = s.matches("^1[3-9]*&");
        System.out.println(matches);
    }
}
