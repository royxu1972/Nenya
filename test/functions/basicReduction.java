package functions;

import Basic.IOFiles;
import Basic.TestSuite;
import Reduction.PostRandomized;
import org.junit.Test;

/**
 * Created by Huayao on 2016/4/22.
 */
public class basicReduction {

    @Test
    public void test_Reduction() {
        // read CA file
        TestSuite ts = IOFiles.readCoveringArrayFromFile("./resources/CA_test.txt");
        System.out.println("old size = " + ts.getTestSuiteSize());

        // run reduction algorithm
        PostRandomized pr = new PostRandomized(ts);
        pr.execution(100, 1);
        System.out.println("new size = " + ts.getTestSuiteSize());

    }

}
