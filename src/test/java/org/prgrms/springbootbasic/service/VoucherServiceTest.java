package org.prgrms.springbootbasic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgrms.springbootbasic.VoucherType;
import org.prgrms.springbootbasic.entity.Voucher;
import org.prgrms.springbootbasic.repository.MemoryVoucherRepository;

class VoucherServiceTest {

    VoucherService voucherService;
    MemoryVoucherRepository memoryVoucherRepository;

    @BeforeEach
    void init() {
        memoryVoucherRepository = new MemoryVoucherRepository();
        voucherService = new VoucherService(memoryVoucherRepository);
    }

    @DisplayName("FixedAmountVoucher 만들기 테스트")
    @Test
    void createFixedAmountVoucher() {
        //given
        int amount = 10;

        //when
        voucherService.createVoucher(VoucherType.FIXED, amount, 0);

        //then
        assertThat(memoryVoucherRepository.getVoucherTotalNumber())
            .isEqualTo(1);
    }

    @DisplayName("PercentAmountVoucher 만들기 테스트")
    @Test
    void createPercentAmountVoucher() {
        //given
        int percent = 10;

        //when
        voucherService.createVoucher(VoucherType.PERCENT, 0, percent);

        //then
        assertThat(memoryVoucherRepository.getVoucherTotalNumber())
            .isEqualTo(1);
    }

    @DisplayName("Voucher 전체 조회 테스트")
    @Test
    void findAll() {
        //given
        voucherService.createVoucher(VoucherType.FIXED, 1000, 0);
        voucherService.createVoucher(VoucherType.PERCENT, 0, 20);

        //when
        List<Voucher> vouchers = voucherService.findAll();

        //then
        assertThat(vouchers.size())
            .isEqualTo(2);
    }
}