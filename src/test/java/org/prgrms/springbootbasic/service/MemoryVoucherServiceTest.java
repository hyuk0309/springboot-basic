package org.prgrms.springbootbasic.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgrms.springbootbasic.repository.MemoryVoucherRepository;

class MemoryVoucherServiceTest {

    MemoryVoucherService memoryVoucherService;
    MemoryVoucherRepository memoryVoucherRepository;

    @BeforeEach
    void init() {
        memoryVoucherRepository = new MemoryVoucherRepository();
        memoryVoucherService = new MemoryVoucherService(memoryVoucherRepository);
    }

    @DisplayName("FixedAmountVoucher 만들기 테스트")
    @Test
    void createFixedAmountVoucher() {
        //given
        long amount = 10L;

        //when
        memoryVoucherService.createFixedAmountVoucher(amount);

        //then
        assertThat(memoryVoucherRepository.getVoucherTotalNumber())
            .isEqualTo(1);
        assertThat(memoryVoucherRepository.findAll().get(0).discount(100L))
            .isEqualTo(90L);
    }

    @DisplayName("PercentAmountVoucher 만들기 테스트")
    @Test
    void createPercentAmountVoucher() {
        //given
        int percent = 10;

        //when
        memoryVoucherService.createPercentAmountVoucher(percent);

        //then
        assertThat(memoryVoucherRepository.getVoucherTotalNumber())
            .isEqualTo(1);
        assertThat(memoryVoucherRepository.findAll().get(0).discount(100L))
            .isEqualTo(90L);
    }
}