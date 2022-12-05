package src.main.java.retryanalyzer;


import src.main.java.logger.Log;
import org.testng.*;


import src.main.java.exceptions.AutomationError;

import src.main.java.util.ApplicationProperties;

import java.util.List;
import static  src.main.java.util.StringUtil.toStringWithSufix;


// Each method contains an instance of this RetryAnalyzer
public class RetryAnalyzer implements IRetryAnalyzer {

    private int count = 0;

    @Override
    public boolean retry(ITestResult result) {
        boolean shouldRetry = shouldRetry(result);
        if (shouldRetry) {
            Log.info("Retrying [" + result.getName() + "] " + toStringWithSufix(count + 1) + " time.", true);
            setRetryCount(count + 1);
            // correct failed invocation numbers for data driven test case.
            List<Integer> failedInvocations = result.getMethod().getFailedInvocationNumbers();
            if (null != failedInvocations && !failedInvocations.isEmpty()) {
                int lastFailedIndex = failedInvocations.size() - 1;
                failedInvocations.remove(lastFailedIndex);
            }
        }
        return shouldRetry;
    }

    public boolean shouldRetry(ITestResult result) {
        Throwable reason = result.getThrowable();

        return (result.getStatus() == ITestResult.FAILURE || result.getStatus() == ITestResult.SKIP) && reason != null
                && !(reason instanceof AutomationError )
                && !(reason instanceof AssertionError )
                && (ApplicationProperties.RETRY_CNT.getIntVal(0) > count);
    }

    public void setRetryCount(int count){
        this.count = count;
    }

    public int getRetryCount()
    {
        return count;
    }
}
