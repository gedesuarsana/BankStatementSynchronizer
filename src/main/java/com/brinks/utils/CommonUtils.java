package com.brinks.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonUtils {

public static List<String> splitSwiftMessage(String content){
    List<String> output = new ArrayList<>();

    List<String> items = Arrays.asList(content.split("\\{1:"));

    for(String item: items){
        if(!item.isBlank()){
            output.add("{1:"+item);
        }
    }
    return output;
}

}
