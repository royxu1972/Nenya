package function;

import Basic.IOFiles;
import Model.TestSuite;
import Reduction.PostRandomized;
import org.junit.Test;

public class TestReduction {

    @Test
    public void test_Reduction() {
        // read CA file
        TestSuite ts = IOFiles.readCoveringArrayFromFile("./resources/CA_test.txt");
        System.out.println("old size = " + ts.testSuiteSize());

        // run reduction algorithm
        PostRandomized pr = new PostRandomized(ts);
        pr.execution(100);
        System.out.println("new size = " + ts.testSuiteSize());
    }

}
