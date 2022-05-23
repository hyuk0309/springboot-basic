package org.prgrms.springbootbasic.controller;

import java.util.function.Function;
import org.prgrms.springbootbasic.controller.console.ConsoleController;

public enum Menu {

    EXIT(consoleController -> false),
    CREATE(consoleController -> {
        consoleController.createVoucher();
        return true;
    }),
    LIST(consoleController -> {
        consoleController.printList();
        return true;
    }),
    BLACKLIST(consoleController -> {
        consoleController.printBlackList();
        return true;
    }),
    CREATE_CUSTOMER(consoleController -> {
        consoleController.createCustomer();
        return true;
    }),
    LIST_CUSTOMER(consoleController -> {
        consoleController.printAllCustomers();
        return true;
    }),
    ASSIGN_VOUCHER(consoleController -> {
        consoleController.assignVoucher();
        return true;
    }),
    LIST_CUSTOMER_VOUCHER(consoleController -> {
        consoleController.listCustomerVoucher();
        return true;
    }),
    DELETE_CUSTOMER_VOUCHER(consoleController -> {
        consoleController.deleteCustomerVoucher();
        return true;
    }),
    LIST_CUSTOMER_HAVING_SPECIFIC_VOUCHER_TYPE(consoleController -> {
        consoleController.listCustomerHavingSpecificVoucherType();
        return true;
    });

    private final Function<ConsoleController, Boolean> process;

    Menu(Function<ConsoleController, Boolean> process) {
        this.process = process;
    }

    public boolean apply(ConsoleController consoleController) {
        return process.apply(consoleController);
    }
}
