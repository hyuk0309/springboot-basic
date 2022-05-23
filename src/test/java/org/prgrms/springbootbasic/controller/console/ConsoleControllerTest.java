package org.prgrms.springbootbasic.controller.console;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prgrms.springbootbasic.controller.Menu;
import org.prgrms.springbootbasic.controller.VoucherType;
import org.prgrms.springbootbasic.entity.customer.Email;
import org.prgrms.springbootbasic.entity.customer.Name;
import org.prgrms.springbootbasic.service.CustomerService;
import org.prgrms.springbootbasic.service.VoucherService;
import org.prgrms.springbootbasic.view.View;

@ExtendWith(MockitoExtension.class)
class ConsoleControllerTest {

    @Mock
    View view;

    @Mock
    VoucherService voucherService;

    @Mock
    CustomerService customerService;

    ConsoleController consoleController;

    @BeforeEach
    void init() {
        consoleController = new ConsoleController(voucherService, view, customerService);
    }

    @DisplayName("EXIT 테스트")
    @Test
    void exit() {
        //given
        when(view.inputMenu()).thenReturn(Menu.EXIT);

        //when
        boolean actual = consoleController.process();

        //then
        assertThat(actual).isFalse();
    }

    @DisplayName("LIST 테스트")
    @Test
    void list() {
        //given
        when(view.inputMenu()).thenReturn(Menu.LIST);

        //when
        consoleController.process();

        //then
        var inOrder = inOrder(view, voucherService);
        inOrder.verify(view).inputMenu();
        inOrder.verify(voucherService).findAll();
        inOrder.verify(view).printList(anyList());
    }

    @DisplayName("FixedAmountVoucher 생성 테스트")
    @Test
    void createFixedAmountVoucher() {
        //given
        when(view.inputMenu()).thenReturn(Menu.CREATE);
        when(view.selectVoucherType()).thenReturn(VoucherType.FIXED);

        //when
        consoleController.process();

        //then
        var inOrder = inOrder(view, voucherService);
        inOrder.verify(view).inputMenu();
        inOrder.verify(view).selectVoucherType();
        inOrder.verify(view).selectAmount();
        inOrder.verify(voucherService).createVoucher(any(VoucherType.class), anyInt(), anyInt());
    }

    @DisplayName("PercentAmountVoucher 생성 테스트")
    @Test
    void createPercentAmountVoucher() {
        //given
        when(view.inputMenu()).thenReturn(Menu.CREATE);
        when(view.selectVoucherType()).thenReturn(VoucherType.PERCENT);

        //when
        consoleController.process();

        //then
        var inOrder = inOrder(view, voucherService);
        inOrder.verify(view).inputMenu();
        inOrder.verify(view).selectVoucherType();
        inOrder.verify(view).selectPercent();
        inOrder.verify(voucherService).createVoucher(any(VoucherType.class), anyInt(), anyInt());
    }

    @DisplayName("BlackList 조회 테스트")
    @Test
    void blackList() {
        //given
        when(view.inputMenu()).thenReturn(Menu.BLACKLIST);

        //when
        consoleController.process();

        //then
        var inOrder = inOrder(view);
        inOrder.verify(view).inputMenu();
        inOrder.verify(view).printCustomerBlackList();
    }

    @DisplayName("createCustomer 테스트")
    @Test
    void createCustomer() {
        //given
        when(view.inputMenu()).thenReturn(Menu.CREATE_CUSTOMER);
        when(view.selectName()).thenReturn("test");
        when(view.selectEmail()).thenReturn("test@gmail.com");

        //when
        consoleController.process();

        //then
        var inOrder = inOrder(view, customerService);
        inOrder.verify(view).inputMenu();
        inOrder.verify(view).selectName();
        inOrder.verify(view).selectEmail();
        inOrder.verify(customerService).createCustomer(any(Name.class), any(Email.class));
    }

    @DisplayName("assignVoucher 테스트 - 정상")
    @Test
    void assignVoucher() {
        //given
        when(view.inputMenu()).thenReturn(Menu.ASSIGN_VOUCHER);
        when(view.selectVoucherId()).thenReturn(UUID.randomUUID());
        when(view.selectCustomerId()).thenReturn(UUID.randomUUID());

        //when
        consoleController.process();

        //then
        var inOrder = inOrder(view, voucherService);
        inOrder.verify(view).selectVoucherId();
        inOrder.verify(view).selectCustomerId();
        inOrder.verify(voucherService).assignVoucherToCustomer(any(UUID.class), any(UUID.class));
    }

    @DisplayName("listCustomerVoucher 테스트 - 정상 케이스")
    @Test
    void listCustomerVoucher() {
        //given
        when(view.inputMenu()).thenReturn(Menu.LIST_CUSTOMER_VOUCHER);
        UUID customerId = UUID.randomUUID();
        when(view.selectCustomerId()).thenReturn(customerId);
        when(voucherService.findCustomerVoucher(customerId)).thenReturn(Collections.emptyList());

        //when
        consoleController.process();

        //then
        var inOrder = inOrder(view, voucherService);
        inOrder.verify(view).selectCustomerId();
        inOrder.verify(voucherService).findCustomerVoucher(customerId);
    }

    @DisplayName("deleteCustomerVoucher 테스트 - 정상 케이스")
    @Test
    void deleteCustomerVoucher() {
        //given
        when(view.inputMenu()).thenReturn(Menu.DELETE_CUSTOMER_VOUCHER);
        when(view.selectCustomerId()).thenReturn(UUID.randomUUID());
        when(view.selectVoucherId()).thenReturn(UUID.randomUUID());

        //when
        consoleController.process();

        //then
        var inOrder = inOrder(view, customerService);
        inOrder.verify(view).selectCustomerId();
        inOrder.verify(view).selectVoucherId();
        inOrder.verify(customerService).deleteVoucher(any(UUID.class), any(UUID.class));
    }

    @DisplayName("listCustomerHavingSpecificVoucherType 테스트")
    @Test
    void listCustomerHavingSpecificVoucherType() {
        //given
        when(view.inputMenu()).thenReturn(Menu.LIST_CUSTOMER_HAVING_SPECIFIC_VOUCHER_TYPE);
        when(view.selectVoucherType()).thenReturn(VoucherType.FIXED);
        when(customerService.findCustomerHavingSpecificVoucherType(VoucherType.FIXED))
            .thenReturn(Collections.emptyList());

        //when
        consoleController.process();

        //then
        var inOrder = inOrder(view, customerService);
        inOrder.verify(view).selectVoucherType();
        inOrder.verify(customerService).findCustomerHavingSpecificVoucherType(VoucherType.FIXED);
        inOrder.verify(view).printAllCustomers(anyList());
    }
}