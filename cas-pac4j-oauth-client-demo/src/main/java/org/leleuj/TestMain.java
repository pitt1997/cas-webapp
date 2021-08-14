package org.leleuj;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class TestMain {

    public static void main(String[] args) throws ParseException {
        // Date类型转String类型
        String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        System.out.println(date); // 输出 2021-05-01 01:01:01

        // String类型转Date类型
        Date date1 = DateUtils.parseDate("2021-05-01 01:01:01", "yyyy-MM-dd HH:mm:ss");

        // 计算一个小时后的日期
        date1 = DateUtils.addHours(new Date(), 1);

        ImmutablePair<Integer, String> pair = test04();
        System.out.println("Left:" + pair.getLeft());
        System.out.printf("Right:" + pair.getRight());

    }

    private static ImmutablePair test04() {
        // 返回两个字段
        ImmutablePair<Integer, String> pair = ImmutablePair.of(1, "yideng");
        System.out.println(pair.getLeft() + "," + pair.getRight()); // 输出 1,yideng
        // 返回三个字段
        ImmutableTriple<Integer, String, Date> triple = ImmutableTriple.of(1, "yideng", new Date());
        System.out.println(triple.getLeft() + "," + triple.getMiddle() + "," + triple.getRight()); // 输出 1,yideng,Wed Apr
        return pair;
    }

    public static void test01() {
        List<String> list1 = new ArrayList<>();
        list1.add("a");
        list1.add("b");
        list1.add("c");
        List<String> list2 = new ArrayList<>();
        list2.add("a");
        list2.add("b");
        list2.add("d");
        list1.retainAll(list2);
        System.out.println(list1); // 输出[a, b]
    }





    private static void test00() {
        HashMap<Integer, String> map = new HashMap<>();
        map.put(null, "");

        map.put(null, "1234");


        map.keySet().forEach(tmp -> {

            System.out.println("key:" + tmp);
            System.out.printf(map.get(tmp));
        });
        System.out.println();


        int MAXIMUM_CAPACITY = 1 << 30;

        System.out.println(MAXIMUM_CAPACITY);

        int MINIMUM_CAPACITY = 1 >> 30;

        System.out.println(MAXIMUM_CAPACITY);
        System.out.println(MINIMUM_CAPACITY);


        System.out.println("==============");

        System.out.println(1 << 2);

    }


    private static void test02() {
        // 如何把list集合拼接成以逗号分隔的字符串 a,b,c
        List<String> list = Arrays.asList("a", "b", "c");
        // 第一种方法，可以用stream流
        String join = list.stream().collect(Collectors.joining(","));
        System.out.println(join); // 输出 a,b,c
        // 第二种方法，其实String也有join方法可以实现这个功能
        join = String.join(",", list);
        System.out.println(join); // 输出 a,b,c
    }


}
