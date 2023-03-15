package com.brinks.utils;

import java.util.Comparator;

public class InvoiceComparator implements Comparator<String> {
    @Override
    public int compare(String item1, String item2) {

        String numberOnly1 = item1.replaceAll("[^0-9]", "");
        String numberOnly2 = item2.replaceAll("[^0-9]", "");

        int number1 = Integer.parseInt(numberOnly1);
        int number2 = Integer.parseInt(numberOnly2);

        if(number1==number2){
            return 0;
        }
        else if(number1<number2){
            return -1;
        }else{
            return 1;
        }
    }
}
