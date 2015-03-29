/**
 * Created by simonnnnnnn on 2015-03-20.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.round;


public class Simon {

    static int[] vr;
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        /*while (true) {
            System.out.print("What you want");
            System.out.println();
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String readIn = null;
            try {
                readIn = br.readLine();
            } catch (IOException ioe)
                System.out.println("IO error trying to read your name!");
                System.exit(1);
            }
            System.out.println();


            String[][] lol = getStockInfo("ASK", "AAPL");
            for (int i = 0; i < lol.length; i++) {
                for (String s : lol[i]) {
                    System.out.println(s);
                }
            }

            System.out.println(getInfo(readIn));
        }*/

        int a, b, c;
        Random r = new Random();
        r.nextInt(10);

        //for(int i=0;i<9;i++){
        makeTrans();

    }

    public static void makeTrans(){
        String stock[]=getStocksNames();
        int n=stock.length;
        double[] buyPrices = new double[n];
        double[] diff= new double[n];
        vr = new int[n];
        initializer();
        while (true) {
            try {
                for (int i = stock.length - 1; i >= 0; i--) {
                    //printing
                    System.out.println(vr[i]);
                    //things
                    String s = stock[i];
                    double buyPrice = buyPrices[i];
                    double currentPrice = getBid(s);
                    diff[i] = buyPrice - currentPrice;

                    int volume = round(getVolume(s) / currentPrice);
                    String[] resp = getInfo("MY_CASH").split(" ");
                    if (resp.length < 1 || !(resp[1].equals("have"))) {
                        double a = Double.parseDouble(getInfo("MY_CASH").split(" ")[1]) * 0.2;
                        if (currentPrice <= a) {
                            vr[i] = volume;
                            String st = getOrder(s, "BID", getAsk(s) - getAsk(s) * 0.002, vr[i]);
                            String responce = getInfo(st);
                            buyPrices[i] = currentPrice;
                        }
                        if (currentPrice > buyPrice * 1.003 || currentPrice  * 1.09 < buyPrice|| (diff[i] / 0.6) > buyPrice) {
                            //sell all
                            getInfo("CLEAR_ASK ".concat(s));
                            getInfo(getOrder(s, "ASK", currentPrice, vr[i] / 2));
                            vr[i] = vr[i];
                            System.out.println(s);
                            System.out.println("Sell all");
                            // buy at low right after
                            buyPrice = currentPrice;
                            vr[i] = round(getVolume(s) / buyPrice);
                            getInfo(getOrder(s, "BID", buyPrice - currentPrice * 0.002, vr[i]));
                        }

                    }

                }
            }catch (Exception e){}
        }


    }
    protected static String[] getStocksNames(){
        String[] stockInfo = getInfo("SECURITIES").split(" ");
        LinkedList<String> array = new LinkedList<String>();
        for (int i = 1; i<stockInfo.length-1; i++){
            if(i%4==1){
                array.add(stockInfo[i]);
            }
        }
        return array.toArray(new String[((int)(stockInfo.length/4))]);
    }
    protected static void initializer(){
        String[] stockInfo = getInfo("MY_SECURITIES").split(" ");
        LinkedList<String> ls = new LinkedList<String>();
        for (int i = 1; i < stockInfo.length; i++){
            ls.add(stockInfo[i]);
        }
        ls.remove(0);
        LinkedList<String> array = new LinkedList<String>();
        for (int i = 0; i<ls.size()-1; i++){
            if(i%3==0){
                vr[(int)(i/3)]=(int)Double.parseDouble(ls.get(i));
            }
        }
    }

    private static int round(double d){
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if(result<0.5){
            return d<0 ? -i : i;
        }else{
            return d<0 ? -(i+1) : i+1;
        }
    }

    //return the lowest bid price
    public static double getBid (String s){
        String temp[][]=getStockInfo("BID",s);
        int l=(temp.length);
        return Double.parseDouble(temp[l/2-l/4][1]);
    }
    public static double getAsk (String s){
        String temp[][]=getStockInfo("ASK",s);
        int l=(temp.length);
        if(l>0) {
            return Double.parseDouble(temp[l-l/2][1]);
        }else {
            return 10;
        }

    }
    //returns the money that is free to use
    public static double getVolume (String s){
        double a;
        try {
            a = Double.parseDouble(getInfo("MY_CASH").split(" ")[1]) * 0.3;
        }catch(Exception e){
            a = 0;
        }
        return a;
    }
    public static String getOrder(String s, String o, double p, int v ){
        return o.concat(" ").concat(s).concat(" ").concat(Double.toString(p)).concat(" ").concat(Integer.toString(v));
    }


    // getOrder returns a string that can be used for getInfo to either
    //      make ask or bid





    public static String getInfo(String arg) {
        String line = "";
        try {
            Socket socket = new Socket("codebb.cloudapp.net", 17429);
            PrintWriter pout = new PrintWriter(socket.getOutputStream());
            BufferedReader bin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pout.println("cfm_hacker" + " " + "cfmbestfm123");
            pout.println(arg);
            pout.println("CLOSE_CONNECTION");
            pout.flush();
            line = bin.readLine();
            pout.close();
            bin.close();
        } catch (IOException e) {
        }
        return line;
    }


    /*
    /**
         * Returns an array of an array that will have the length of the number
         * of ASK/BID and at each [0] will be price and [1] will be volume
         * ex. String[][] awesome = getStockInfo("ASK","AAPL");
         * @param type "ASK" or "BID"
         * @param name the stock name in CAPITAL letters
         * @return String[][]
         */
    protected static String[][] getStockInfo(String type, String name) {
        String[] stockInfo = getInfo("ORDERS ".concat(name)).split(" ");
        LinkedList<String[]> array = new LinkedList<String[]>();
        for (int i = 1; i < stockInfo.length - 1; i++) {

            if (i % 3 == 1) {
                if (stockInfo[i].equals(type)) {
                    array.add(new String[]{stockInfo[i + 1], stockInfo[i + 2]});
                }
            }
        }
        String[][] returned = array.toArray(new String[array.size()][2]);
        ;
        array.toArray(returned);
        return returned;
    }
}

