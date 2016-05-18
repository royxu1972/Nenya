package function;

import Basic.IOFiles;
import Basic.TestSuite;
import Reduction.PostRandomized;
import org.junit.Test;

public class reduction {

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
