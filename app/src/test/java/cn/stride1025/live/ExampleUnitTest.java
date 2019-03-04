package cn.stride1025.live;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testt() {

        char [] chars = {'a','s','f'};
        Arrays.sort(chars);
        int search = Arrays.binarySearch(chars, 'f');
        System.out.print("-- > " + search);

    }


}