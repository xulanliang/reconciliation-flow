package com.yiban;


public class JavaException

{

         public static void main(String[] args)

         {

                   System.out.println(test());

         }

         public static int test()

         {

                   int i=0;

                   try

                   {

                            i = 1/0;//抛出非受检异常

                            i=100;

                            System.out.println("in try 抛出异常后不会执行到这里！");

                   }

                   catch(Exception e)

                   {

                            i=1;

                            System.out.println("in catch i="+i);

                            return i;     //注释1

                   }

                   finally

                   {

                            i=2;

                            System.out.println("in finally i="+i);

                   }

                   i=3;

                   return i;

         }

}
