package com.dragonslair.bcintredux.bigcommerce;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BigCommerceQuotaManager {

    // Header keys
    private static final String requestsRemainingKey = "X-Rate-Limit-Requests-Left";
    private static final String timeToResetMsKey = "X-Rate-Limit-Time-Reset-Ms";
    private static final String requestsQuotaKey = "X-Rate-Limit-Requests-Quota";
    private static final String timeInWindowKey = "X-Rate-Limit-Time-Window-Ms";

    // Reference vals for quota info
    private AtomicInteger requestsRemaining = new AtomicInteger(1);
    private AtomicInteger timeToResetMs = new AtomicInteger(0);
    private AtomicInteger requestsQuota = new AtomicInteger(1);
    private AtomicInteger timeInWindowMs = new AtomicInteger(1);

    public BigCommerceQuotaManager() {

    }

    public void updateValues(HttpHeaders headers) {
        if (headers.containsKey(requestsRemainingKey)) {
            requestsRemaining.set(Integer.parseInt(headers.get(requestsRemainingKey).get(0)));
        }
        if (headers.containsKey(timeToResetMsKey)) {
            timeToResetMs.set(Integer.parseInt(headers.get(timeToResetMsKey).get(0)));
        }
        if (headers.containsKey(requestsQuotaKey)) {
            requestsQuota.set(Integer.parseInt(headers.get(requestsQuotaKey).get(0)));
        }
        if (headers.containsKey(timeInWindowKey)) {
            timeInWindowMs.set(Integer.parseInt(headers.get(timeInWindowKey).get(0)));
        }
    }

    public void blockForQuota(ClientRequest request) {
        if (requestsRemaining.getAcquire() < 1) {
            try {
                Thread.sleep(timeToResetMs.addAndGet(1000));
            } catch (InterruptedException ie) {
                throw new BigCommerceServiceException("A thread was interrupted waiting to run request: "
                        + request.method()
                        + " "
                        + request.url());
            }
        }
    }
}
