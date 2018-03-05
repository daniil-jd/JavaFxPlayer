package player.utils;

import com.sun.org.apache.xml.internal.utils.res.StringArrayWrapper;

public class TestSwitch {
    private static void switchString (String a, String b) {
        String c = a;
        a = b;
        b = c;
    }

    private static void switchWrapper (Wrapper a, Wrapper b) {
        Wrapper c = new Wrapper(a.getWrapper());
        a.setWrapper(b.getWrapper());
        b.setWrapper(c.getWrapper());
    }

    private static void chng (Integer a, Integer b) {
        Integer c = a;
        a = b;
        b = c;
    }

    private static void chng (IntWrppr a, IntWrppr b) {
        IntWrppr c = new IntWrppr(a.getIn());
        a.setIn(b.getIn());
        b.setIn(c.getIn());
    }

    public static void main(String[] args) {
        String a = "1", b = "2";
        System.out.println("at begining a = " + a + "; b = " + b);
        switchString(a,b);
        System.out.println("end: " + a + " " + b);
        System.out.println("------------------------");
        Wrapper a1 = new Wrapper(a);
        Wrapper b1 = new Wrapper(b);
        System.out.println("begin " + a1.getWrapper() + " " + b1.getWrapper());
        switchWrapper(a1, b1);
        System.out.println("end " + a1.getWrapper() + " " + b1.getWrapper());
        System.out.println("------------------------");
        Integer aa = 1, bb = 2;
        System.out.println("begin " + aa + " " + bb);
        chng(aa, bb);
        System.out.println("end " + aa + " " + bb);
        System.out.println("------------------------");
        IntWrppr a2 = new IntWrppr(1), b2 = new IntWrppr(2);
        System.out.println("begin " + a2 + " " + b2);
        chng(a2, b2);
        System.out.println("end " + a2 + " " + b2);
    }
}

class Wrapper {
    private String wrapper;

    public Wrapper(String in) {
        this.wrapper = in;
    }

    public String getWrapper() {
        return wrapper;
    }

    public void setWrapper(String wrapper) {
        this.wrapper = wrapper;
    }
}

class IntWrppr {
    private Integer in;

    public IntWrppr(Integer in) {
        this.in = in;
    }

    public Integer getIn() {
        return in;
    }

    public void setIn(Integer in) {
        this.in = in;
    }

    @Override
    public String toString() {
        return "" + in;
    }
}
