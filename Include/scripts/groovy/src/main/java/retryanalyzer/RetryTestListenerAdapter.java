package src.main.java.retryanalyzer;

import org.testng.*;

import src.main.java.logger.Log;

import java.util.HashSet;
import java.util.Set;

public class RetryTestListenerAdapter extends TestListenerAdapter {

    @Override
    public void onTestSkipped(ITestResult result)
    {
        super.onTestSkipped(result);
        setRetryValues(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        super.onTestFailure(result);
        setRetryValues(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        super.onTestSuccess(result);
        setRetryValues(result);

        Set<ITestResult> testResults = result.getTestContext().getSkippedTests().getResults(result.getMethod());
        for (ITestResult skippedResult : testResults) {
            result.getTestContext().getSkippedTests().removeResult(skippedResult);
            Log.LOGGER.error(result.getMethod().getQualifiedName() + "() was retried and passed, removing retried results.");
        }
    }

    @Override
    public void onFinish(ITestContext testContext) {

        Set<ITestResult> results = new HashSet<>();
        results.addAll(testContext.getFailedTests().getAllResults());
        results.addAll(testContext.getSkippedTests().getAllResults());
        results.addAll(testContext.getPassedTests().getAllResults());

        for (ITestResult result : results) {
        	if(((RetryAnalyzer)result.getMethod().getRetryAnalyzer()).getRetryCount()>0)
           // if(((RetryAnalyzer)result.getMethod().getRetryAnalyzer(result)).getRetryCount() > 0)
            {
                Log.LOGGER.error(result.getMethod().getQualifiedName() + "() was retried");
            }
        }
    }

    private void setRetryValues(ITestResult result)
    {
        Reporter.setCurrentTestResult(result);
    }
}
