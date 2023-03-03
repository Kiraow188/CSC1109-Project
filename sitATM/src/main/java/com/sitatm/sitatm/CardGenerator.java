package com.sitatm.sitatm;
import java.util.Arrays;
import java.util.Random;

public class CardGenerator {
    public static String generateCardNumber(int cardType){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        if (cardType == 1){
            sb.append(4);
        }
        else if (cardType == 2){
            sb.append(5);
        }
        else{
            System.out.println("Invalid card type!");
        }

        // generate first 15 digits randomly
        for (int i = 0; i < 14; i++) {
            sb.append(random.nextInt(10));
        }

        int[] CCIntArray = new int[16];
        for(int i = 0; i < 15; i++) {
            CCIntArray[i] = Character.getNumericValue(sb.charAt(i));
        }

        for(int i = CCIntArray.length - 2; i >= 0; i = i - 2) {
            int num = CCIntArray[i];
            num = num * 2;  // step 1
            if(num > 9) {
                num = num % 10 + num / 10;  // step 2
            }
            CCIntArray[i] = num;
        }

        int sum = sumDigits(CCIntArray);  // step 3

        int remainder = sum % 10;
        remainder = (remainder == 0) ? 0 : 10 - remainder;  // calculate check digit

        sb.append(remainder);
        String str = sb.toString();
        return str;
    }

    public static boolean isValidCreditCardNumber(String cardNumber) {
        // int array for processing the cardNumber
        int[] cardIntArray=new int[cardNumber.length()];

        for(int i = 0; i < cardNumber.length(); i++) {
            char c = cardNumber.charAt(i);
            if (Character.isDigit(c)) {
                cardIntArray[i] = Character.getNumericValue(c);
            }
        }

        for(int i=cardIntArray.length-2;i>=0;i=i-2)
        {
            int num = cardIntArray[i];
            num = num * 2;  // step 1
            if(num>9)
            {
                num = num%10 + num/10;  // step 2
            }
            cardIntArray[i]=num;
        }

        int sum = sumDigits(cardIntArray);  // step 3

        //System.out.println(sum);

        if(sum%10==0)  // step 4
        {
            System.out.println("Card is vaild: "+sum);
            return true;
        }
        System.out.println("Card is invalid: "+sum);
        return false;
    }

    public static int sumDigits(int[] arr){
        return Arrays.stream(arr).sum();
    }

    /* Debug
    public static void main (String[] args){
        String cardNumberVisa = generateCardNumber(1);
        String cardNumberMasters = generateCardNumber(2);
        isValidCreditCardNumber(cardNumberVisa);
        isValidCreditCardNumber(cardNumberMasters);
        System.out.println(cardNumberVisa);
        System.out.println(cardNumberMasters);
    }
     */
}
