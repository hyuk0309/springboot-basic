package org.prgrms.springbootbasic.entity.voucher;

import java.util.UUID;
import org.prgrms.springbootbasic.exception.PercentRangeMaxException;
import org.prgrms.springbootbasic.exception.PercentRangeMinException;

public class PercentDiscountVoucher extends Voucher {

    public static final int MIN_RANGE = 0;
    public static final int MAX_RANGE = 100;

    private final int percent;

    public PercentDiscountVoucher(UUID voucherId, int percent) {
        super(voucherId);

        validatePercentRange(percent);
        this.percent = percent;
    }

    public PercentDiscountVoucher(UUID voucherId, UUID customerId, int percent) {
        super(voucherId, customerId);

        validatePercentRange(percent);
        this.percent = percent;
    }

    private void validatePercentRange(int percent) {
        if (percent <= MIN_RANGE) {
            throw new PercentRangeMinException();
        }
        if (percent > MAX_RANGE) {
            throw new PercentRangeMaxException();
        }
    }

    public int getPercent() {
        return percent;
    }
}